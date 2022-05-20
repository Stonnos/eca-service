package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import com.ecaservice.core.redelivery.callback.RetryCallback;
import com.ecaservice.core.redelivery.converter.RequestMessageConverter;
import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.error.ExceptionStrategy;
import com.ecaservice.core.redelivery.model.MethodInfo;
import com.ecaservice.core.redelivery.model.RetryContext;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
import com.ecaservice.core.redelivery.strategy.RetryStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Retry service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RetryService {

    private final RetryRequestCacheService retryRequestCacheService;
    private final ApplicationContext applicationContext;
    private final RetryRequestRepository retryRequestRepository;

    private final Map<String, MethodInfo> declaredRedeliverMethodsCache = new ConcurrentHashMap<>(256);

    /**
     * Retry specified request.
     *
     * @param retryRequest - retry request entity
     */
    public void retry(RetryRequest retryRequest) {
        log.info("Starting to resent retry request [{}] with id [{}]", retryRequest.getRequestType(),
                retryRequest.getId());
        var retryMethodInfo = getRetryMethodInfo(retryRequest);
        invokeRetryMethod(retryRequest, retryMethodInfo.getBean(), retryMethodInfo.getMethod());
    }

    private MethodInfo getRetryMethodInfo(RetryRequest retryRequest) {
        log.debug("Starting to find method annotated with [{}], code [{}]", Retry.class.getSimpleName(),
                retryRequest.getRequestType());
        var methodInfo = declaredRedeliverMethodsCache.get(retryRequest.getRequestType());
        if (methodInfo == null) {
            log.debug("Method annotated with [{}], code [{}] not found in cache", Retry.class.getSimpleName(),
                    retryRequest.getRequestType());
            var beans = applicationContext.getBeansWithAnnotation(Retryable.class);
            if (CollectionUtils.isEmpty(beans)) {
                throw new IllegalStateException(
                        String.format("No one bean found with annotation [%s]", Retryable.class.getSimpleName()));
            }
            var retryMethods = getAllRetryMethodsWithCode(beans, retryRequest.getRequestType());
            if (CollectionUtils.isEmpty(retryMethods)) {
                throw new IllegalStateException(
                        String.format("No one method found annotated with [%s], code [%s]", Retry.class.getSimpleName(),
                                retryRequest.getRequestType()));
            }
            int retryMethodsSize = retryMethods.size();
            log.debug("Found [{}] methods annotated with [{}], code [{}]", retryMethodsSize,
                    Retry.class.getSimpleName(), retryRequest.getRequestType());
            if (retryMethodsSize > 1) {
                throw new IllegalStateException(
                        String.format("[%d] methods found annotated with [%s], code [%s]", retryMethodsSize,
                                Retry.class.getSimpleName(), retryRequest.getRequestType()));
            }
            var retryMethodInfo = retryMethods.iterator().next();
            Object retryBean = retryMethodInfo.getBean();
            var retryMethod = retryMethodInfo.getMethod();
            if (retryMethod.getParameters().length == 0) {
                throw new IllegalStateException(
                        String.format("[%s#%s] annotated with [%s], code [%s], has no input parameters",
                                retryBean.getClass().getSimpleName(), retryMethod.getName(),
                                Retry.class.getSimpleName(), retryRequest.getRequestType()));
            }
            methodInfo = new MethodInfo(retryBean, retryMethod);
            declaredRedeliverMethodsCache.put(retryRequest.getRequestType(), methodInfo);
            log.debug("Method [{}#{}] annotated with [{}], code [{}] has been put into cache",
                    methodInfo.getBean().getClass().getSimpleName(), methodInfo.getMethod().getName(),
                    Retry.class.getSimpleName(), retryRequest.getRequestType());
        }
        log.debug("Method [{}#{}] annotated with [{}], code [{}] has been found",
                methodInfo.getBean().getClass().getSimpleName(), methodInfo.getMethod().getName(),
                Retry.class.getSimpleName(), retryRequest.getRequestType());
        return methodInfo;
    }

    private List<MethodInfo> getAllRetryMethodsWithCode(Map<String, Object> beans, String code) {
        List<MethodInfo> result = newArrayList();
        for (var entry : beans.entrySet()) {
            Class<?> targetClass = AopUtils.getTargetClass(entry.getValue());
            var methods = ReflectionUtils.getDeclaredMethods(targetClass);
            var retryMethods = getRetryMethods(methods, code);
            log.debug("Found [{}] methods with code [{}] annotated with [{}] for bean [{}]", retryMethods.size(),
                    code, Retry.class.getSimpleName(), entry.getValue().getClass().getSimpleName());
            if (!CollectionUtils.isEmpty(retryMethods)) {
                retryMethods.forEach(method -> result.add(new MethodInfo(entry.getValue(), method)));
            }
        }
        return result;
    }

    private List<Method> getRetryMethods(Method[] methods, String code) {
        return Stream.of(methods)
                .filter(method -> {
                    var annotation = AnnotationUtils.findAnnotation(method, Retry.class);
                    return annotation != null && annotation.value().equals(code);
                }).collect(Collectors.toList());
    }

    private void invokeRetryMethod(RetryRequest retryRequest, Object retryBean, Method retryMethod) {
        var retryAnnotation = AnnotationUtils.findAnnotation(retryMethod, Retry.class);
        Assert.notNull(retryAnnotation,
                String.format("[%s] annotation not specified for method [%s#%s]", Retry.class.getSimpleName(),
                        retryBean.getClass().getSimpleName(), retryMethod.getName()));
        //Get real bean under AOP proxy to prevent aspect invocation
        Object targetBean = AopProxyUtils.getSingletonTarget(retryBean);
        Assert.notNull(targetBean,
                String.format("Bean [%s] is not AOP proxy", retryBean.getClass().getSimpleName()));
        var retryCallback = applicationContext.getBean(retryAnnotation.retryCallback(), RetryCallback.class);
        var retryContext = new RetryContext(retryRequest.getRequestId(), retryRequest.getRetries() + 1,
                retryRequest.getMaxRetries());
        try {
            Object request = deserializeRequest(retryRequest.getRequest(), retryMethod, retryAnnotation);
            ReflectionUtils.invokeMethod(retryMethod, targetBean, request);
            log.info("Retry request [{}] with id [{}] has been sent", retryRequest.getRequestType(),
                    retryRequest.getId());
            retryCallback.onSuccess(retryContext);
            retryRequestCacheService.delete(retryRequest);
        } catch (Exception ex) {
            log.error("Error while retry request with id [{}]: {}", retryRequest.getId(),
                    ex.getMessage());
            var exceptionStrategy =
                    applicationContext.getBean(retryAnnotation.exceptionStrategy(), ExceptionStrategy.class);
            if (exceptionStrategy.notFatal(ex)) {
                handleNotFatalError(retryRequest, retryContext, retryCallback, retryAnnotation, ex);
            } else {
                retryCallback.onError(retryContext, ex);
                retryRequestCacheService.delete(retryRequest);
            }
        }
    }

    private Object deserializeRequest(String request, Method method, Retry retry) {
        try {
            var requestMessageConverter =
                    applicationContext.getBean(retry.messageConverter(), RequestMessageConverter.class);
            return requestMessageConverter.convert(request, method.getParameters()[0].getType());
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void handleNotFatalError(RetryRequest retryRequest,
                                     RetryContext retryContext,
                                     RetryCallback retryCallback,
                                     Retry retry,
                                     Exception ex) {
        if (retryRequest.getMaxRetries() > 0 && retryRequest.getRetries() + 1 >= retryRequest.getMaxRetries()) {
            log.info("Exceeded retry request [{}] with id [{}]", retryRequest.getRequestType(),
                    retryRequest.getId());
            retryCallback.onRetryExhausted(retryContext);
            retryRequestCacheService.delete(retryRequest);
        } else {
            retryCallback.onError(retryContext, ex);
            updateErrorRetryRequest(retryRequest, retry);
        }
    }

    private void updateErrorRetryRequest(RetryRequest retryRequest, Retry retry) {
        retryRequest.setRetries(retryRequest.getRetries() + 1);
        LocalDateTime nextRetryAt = calculateNextRetryAt(retryRequest, retry);
        retryRequest.setRetryAt(nextRetryAt);
        retryRequestRepository.save(retryRequest);
    }

    private LocalDateTime calculateNextRetryAt(RetryRequest retryRequest, Retry retry) {
        var retryStrategy = getRetryStrategy(retry);
        long nextRetryIntervalMillis =
                retryStrategy.calculateNextRetryIntervalMillis(retryRequest.getRetries());
        return LocalDateTime.now().plus(nextRetryIntervalMillis, ChronoUnit.MILLIS);
    }

    private RetryStrategy getRetryStrategy(Retry retry) {
        return applicationContext.getBean(retry.retryStrategy(), RetryStrategy.class);
    }
}
