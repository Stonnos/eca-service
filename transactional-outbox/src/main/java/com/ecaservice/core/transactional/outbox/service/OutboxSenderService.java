package com.ecaservice.core.transactional.outbox.service;

import com.ecaservice.core.transactional.outbox.annotation.OutboxSender;
import com.ecaservice.core.transactional.outbox.entity.OutboxMessageEntity;
import com.ecaservice.core.transactional.outbox.error.ExceptionStrategy;
import com.ecaservice.core.transactional.outbox.model.MethodInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static com.ecaservice.common.web.util.JsonUtils.fromJson;

/**
 * Outbox sender service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxSenderService {

    private final ApplicationContext applicationContext;
    private final OutboxSenderProvider outboxSenderProvider;
    private final OutboxMessageService outboxMessageService;

    /**
     * Sent outbox message.
     *
     * @param outboxMessageEntity - retry request entity
     */
    public void sentOutboxMessage(OutboxMessageEntity outboxMessageEntity) {
        log.info("Starting to sent outbox message [{}] with id [{}]", outboxMessageEntity.getMessageCode(),
                outboxMessageEntity.getId());
        MethodInfo senderMethodInfo = outboxSenderProvider.getOutboxSender(outboxMessageEntity.getMessageCode());
        invokeSenderMethod(outboxMessageEntity, senderMethodInfo.getBean(), senderMethodInfo.getMethod());
    }

    private void invokeSenderMethod(OutboxMessageEntity outboxMessageEntity, Object senderBean, Method senderMethod) {
        var senderAnnotation = AnnotationUtils.findAnnotation(senderMethod, OutboxSender.class);
        Assert.notNull(senderAnnotation,
                String.format("[%s] annotation not specified for method [%s#%s]", OutboxSender.class.getSimpleName(),
                        senderBean.getClass().getSimpleName(), senderMethod.getName()));
        try {
            Object request = fromJson(outboxMessageEntity.getMessageBody(), senderMethod.getParameters()[0].getType());
            ReflectionUtils.invokeMethod(senderMethod, senderBean, request);
            log.info("Outbox message [{}] with id [{}] has been sent", outboxMessageEntity.getMessageCode(),
                    outboxMessageEntity.getId());
            outboxMessageService.delete(outboxMessageEntity);
        } catch (Exception ex) {
            log.error("Error while sent outbox message with id [{}]: {}", outboxMessageEntity.getId(), ex.getMessage());
            var exceptionStrategy =
                    applicationContext.getBean(senderAnnotation.exceptionStrategy(), ExceptionStrategy.class);
            if (!exceptionStrategy.notFatal(ex)) {
                outboxMessageService.delete(outboxMessageEntity);
            }
        }
    }
}
