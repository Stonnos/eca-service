package com.ecaservice.core.redelivery.aspect;

import com.ecaservice.common.web.expression.SpelExpressionHelper;
import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.converter.RequestMessageConverter;
import com.ecaservice.core.redelivery.error.ExceptionStrategy;
import com.ecaservice.core.redelivery.metrics.RetryMeterService;
import com.ecaservice.core.redelivery.model.RetryRequestModel;
import com.ecaservice.core.redelivery.service.RetryRequestCacheService;
import com.ecaservice.core.redelivery.strategy.RetryStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * Aspect implements redelivery mechanism.
 *
 * @author Roman Batygin
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RetryAspect {

    private static final int REQUEST_INPUT_PARAM_INDEX = 0;

    private final ApplicationContext applicationContext;
    private final RetryRequestCacheService retryRequestCacheService;
    private final RetryMeterService retryMeterService;
    private final SpelExpressionHelper spelExpressionHelper = new SpelExpressionHelper();

    /**
     * Around method to support redelivery mechanism. If request fails, then it will save in queue for retry sending.
     *
     * @param joinPoint - join point
     * @param retry     - retry annotation
     * @return method result
     * @throws Throwable in case of error
     */
    @Around("execution(@com.ecaservice.core.redelivery.annotation.Retry * * (..)) && @annotation(retry)")
    public Object around(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        log.debug("Starting to around retry method [{}]", joinPoint.getSignature().getName());
        Object request = getRequestParameter(joinPoint.getArgs());
        try {
            var result = joinPoint.proceed();
            log.debug("Around retry method [{}] has been processed", joinPoint.getSignature().getName());
            return result;
        } catch (Exception ex) {
            log.error("Error while sending request: {}", ex.getMessage());
            handleError(retry, joinPoint, request, ex);
            throw ex;
        }
    }

    private void saveRequest(Object request, ProceedingJoinPoint joinPoint, Retry retry) {
        try {
            var requestMessageConverter =
                    applicationContext.getBean(retry.messageConverter(), RequestMessageConverter.class);
            var convertedRequest = requestMessageConverter.convert(request);
            String requestId = getRequestId(joinPoint, retry);
            var retryStrategy = getRetryStrategy(retry);
            var retryRequestModel = RetryRequestModel.builder()
                    .requestType(retry.value())
                    .requestId(requestId)
                    .request(convertedRequest)
                    .maxRetries(retryStrategy.getMaxRetries())
                    .minRetryInterval(retryStrategy.getMinRetryIntervalMillis())
                    .build();
            retryRequestCacheService.save(retryRequestModel);
            retryMeterService.trackRetryRequestCacheSize(retryRequestModel.getRequestType());
        } catch (Exception ex) {
            log.error("Can's save retry request [{}]: {}", retry.value(), ex.getMessage());
        }
    }

    private RetryStrategy getRetryStrategy(Retry retry) {
        return applicationContext.getBean(retry.retryStrategy(), RetryStrategy.class);
    }

    private void handleError(Retry retry, ProceedingJoinPoint joinPoint, Object request, Exception exception) {
        ExceptionStrategy exceptionStrategy =
                applicationContext.getBean(retry.exceptionStrategy(), ExceptionStrategy.class);
        if (exceptionStrategy.notFatal(exception)) {
            saveRequest(request, joinPoint, retry);
        } else {
            retryMeterService.trackRetryRequestError(retry.value());
        }
    }

    private Object getRequestParameter(Object[] inputArgs) {
        if (inputArgs == null || inputArgs.length == 0) {
            throw new IllegalStateException(MessageFormat.format("Empty parameters for method annotated by [{0}]",
                    Retry.class.getSimpleName()));
        }
        Object inputArg = inputArgs[REQUEST_INPUT_PARAM_INDEX];
        if (inputArg == null) {
            throw new IllegalStateException(
                    MessageFormat.format("Got null parameter value at index [{0}] for method annotated by [{1}]",
                            REQUEST_INPUT_PARAM_INDEX, Retry.class.getSimpleName()));
        }
        return inputArg;
    }

    private String getRequestId(ProceedingJoinPoint joinPoint, Retry retry) {
        if (StringUtils.isBlank(retry.requestIdKey())) {
            return null;
        } else {
            Object value = spelExpressionHelper.parseExpression(joinPoint, retry.requestIdKey());
            return String.valueOf(value);
        }
    }
}
