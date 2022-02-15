package com.ecaservice.core.redelivery.service;

import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import com.ecaservice.core.redelivery.converter.RequestMessageConverter;
import com.ecaservice.core.redelivery.entity.RetryRequest;
import com.ecaservice.core.redelivery.error.ExceptionStrategy;
import com.ecaservice.core.redelivery.model.MethodInfo;
import com.ecaservice.core.redelivery.model.MethodsInfo;
import com.ecaservice.core.redelivery.repository.RetryRequestRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис для повторной отправки запросов.
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
        var redeliverMethod = getRetryMethodInfo(retryRequest);
        invokeRetryMethod(retryRequest, redeliverMethod.getBean(), redeliverMethod.getMethod());
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
            int redeliverMethodsSize = retryMethods.values()
                    .stream()
                    .mapToInt(methodsInfo -> methodsInfo.getMethods().size())
                    .sum();
            log.debug("Found [{}] methods annotated with [{}], code [{}]", redeliverMethodsSize,
                    Retry.class.getSimpleName(), retryRequest.getRequestType());
            if (redeliverMethodsSize > 1) {
                throw new IllegalStateException(
                        String.format("[%d] methods found annotated with [%s], code [%s]", redeliverMethodsSize,
                                Retry.class.getSimpleName(), retryRequest.getRequestType()));
            }
            var retryMethodsInfo = retryMethods.values().iterator().next();
            Object retryBean = retryMethodsInfo.getBean();
            var retryMethod = retryMethodsInfo.getMethods().iterator().next();
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

    private Map<String, MethodsInfo> getAllRetryMethodsWithCode(Map<String, Object> beans, String code) {
        Map<String, MethodsInfo> methodsInfoMap = new HashMap<>();
        for (var entry : beans.entrySet()) {
            Class<?> targetClass = AopUtils.getTargetClass(entry.getValue());
            var methods = ReflectionUtils.getDeclaredMethods(targetClass);
            var redeliverMethods = getRetryMethods(methods, code);
            log.debug("Found [{}] methods with code [{}] annotated with [{}] for bean [{}]", redeliverMethods.size(),
                    code, Retry.class.getSimpleName(), entry.getValue().getClass().getSimpleName());
            if (!CollectionUtils.isEmpty(redeliverMethods)) {
                methodsInfoMap.put(entry.getKey(), new MethodsInfo(entry.getValue(), redeliverMethods));
            }
        }
        return methodsInfoMap;
    }

    private List<Method> getRetryMethods(Method[] methods, String code) {
        return Stream.of(methods)
                .filter(method -> {
                    var redeliverAnnotation = AnnotationUtils.findAnnotation(method, Retry.class);
                    return redeliverAnnotation != null && redeliverAnnotation.value().equals(code);
                }).collect(Collectors.toList());
    }

    private void invokeRetryMethod(RetryRequest retryRequest, Object redeliverBean, Method retryMethod) {
        var redeliverAnnotation = AnnotationUtils.findAnnotation(retryMethod, Retry.class);
        Assert.notNull(redeliverAnnotation,
                String.format("[%s] annotation not specified for method [%s#%s]", Retry.class.getSimpleName(),
                        redeliverBean.getClass().getSimpleName(), retryMethod.getName()));
        //Get real bean under AOP proxy to prevent aspect invocation
        Object targetBean = AopProxyUtils.getSingletonTarget(redeliverBean);
        Assert.notNull(targetBean,
                String.format("Bean [%s] is not AOP proxy", redeliverBean.getClass().getSimpleName()));
        Object request = deserializeRequest(retryRequest.getRequest(), retryMethod, redeliverAnnotation);
        try {
            ReflectionUtils.invokeMethod(retryMethod, targetBean, request);
            log.info("Retry request [{}] with id [{}] has been sent", retryRequest.getRequestType(),
                    retryRequest.getId());
            retryRequestCacheService.delete(retryRequest);
        } catch (Exception ex) {
            log.error("Error while retry request with id [{}]: {}", retryRequest.getId(),
                    ex.getMessage());
            ExceptionStrategy exceptionStrategy =
                    applicationContext.getBean(redeliverAnnotation.exceptionStrategy(), ExceptionStrategy.class);
            if (exceptionStrategy.notFatal(ex)) {
                handleError(retryRequest);
            } else {
                retryRequestCacheService.delete(retryRequest);
            }
        }
    }

    private Object deserializeRequest(String request, Method redeliverMethod, Retry retry) {
        try {
            var requestMessageConverter =
                    applicationContext.getBean(retry.messageConverter(), RequestMessageConverter.class);
            return requestMessageConverter.convert(request, redeliverMethod.getParameters()[0].getType());
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void handleError(RetryRequest retryRequest) {
        if (retryRequest.getMaxRetries() > 0 && retryRequest.getRetries() + 1 >= retryRequest.getMaxRetries()) {
            log.info("Exceeded retry request [{}] with id [{}]", retryRequest.getRequestType(),
                    retryRequest.getId());
            retryRequestCacheService.delete(retryRequest);
        } else {
            retryRequest.setRetries(retryRequest.getRetries() + 1);
            retryRequestRepository.save(retryRequest);
        }
    }
}
