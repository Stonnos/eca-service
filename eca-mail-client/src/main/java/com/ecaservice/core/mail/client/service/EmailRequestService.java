package com.ecaservice.core.mail.client.service;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import com.ecaservice.core.lock.annotation.TryLocked;
import com.ecaservice.core.mail.client.config.EcaMailClientProperties;
import com.ecaservice.core.mail.client.entity.EmailRequestEntity;
import com.ecaservice.core.mail.client.entity.EmailRequestStatus;
import com.ecaservice.core.mail.client.repository.EmailRequestRepository;
import com.ecaservice.notification.dto.EmailRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.core.mail.client.config.EcaMailClientAutoConfiguration.MAIL_LOCK_REGISTRY;

/**
 * Audit redelivery service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "mail.client.redelivery", havingValue = "true")
@RequiredArgsConstructor
public class EmailRequestService {

    private final EcaMailClientProperties ecaMailClientProperties;
    private final EmailRequestSender emailRequestSender;
    private final EncryptorBase64AdapterService encryptorBase64AdapterService;
    private final EmailRequestRepository emailRequestRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Redeliver not sent email requests.
     */
    @TryLocked(lockName = "processNotSentEmailRequests", lockRegistry = MAIL_LOCK_REGISTRY)
    public void processNotSentEmailRequests() {
        log.debug("Starting redeliver email requests");
        var ids = emailRequestRepository.findNotSentEmailRequests(LocalDateTime.now());
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("Found [{}] not sent email requests", ids.size());
            processPaging(ids, emailRequestRepository::findByIdIn,
                    emailRequestEntities -> emailRequestEntities.forEach(this::sendEmailRequest));
        }
        log.debug("Redeliver email requests has been finished");
    }

    /**
     * Processed exceeded email requests.
     */
    @TryLocked(lockName = "processExceededEmailRequests", lockRegistry = MAIL_LOCK_REGISTRY)
    public void processExceededEmailRequests() {
        log.debug("Starting to process exceeded email requests");
        var ids = emailRequestRepository.findExceededEmailRequests(LocalDateTime.now());
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("Found [{}] exceeded email requests", ids.size());
            processPaging(ids, emailRequestRepository::findByIdIn, this::processExceededRequests);
        }
        log.debug("Exceeded email requests processing has been finished");
    }

    private void processPaging(List<Long> ids,
                               BiFunction<List<Long>, Pageable, Page<EmailRequestEntity>> nextPageFunction,
                               Consumer<List<EmailRequestEntity>> pageContentAction) {
        Pageable pageRequest = PageRequest.of(0, ecaMailClientProperties.getPageSize());
        Page<EmailRequestEntity> page;
        do {
            page = nextPageFunction.apply(ids, pageRequest);
            if (page == null || !page.hasContent()) {
                log.trace("No one email request has been fetched");
                break;
            } else {
                pageContentAction.accept(page.getContent());
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }

    private void processExceededRequests(List<EmailRequestEntity> emailRequestEntities) {
        emailRequestEntities.forEach(
                emailRequestEntity -> emailRequestEntity.setRequestStatus(EmailRequestStatus.EXCEEDED));
        emailRequestRepository.saveAll(emailRequestEntities);
        log.info("[{}] email requests has been marked as exceeded", emailRequestEntities.size());
    }

    private EmailRequest getEmailRequest(EmailRequestEntity emailRequestEntity) throws JsonProcessingException {
        String requestJson;
        if (emailRequestEntity.isEncrypted()) {
            requestJson = encryptorBase64AdapterService.decrypt(emailRequestEntity.getRequestJson());
        } else {
            requestJson = emailRequestEntity.getRequestJson();
        }
        return objectMapper.readValue(requestJson, EmailRequest.class);
    }

    private void sendEmailRequest(EmailRequestEntity emailRequestEntity) {
        putMdc(TX_ID, emailRequestEntity.getTxId());
        try {
            var emailRequest = getEmailRequest(emailRequestEntity);
            emailRequestSender.sendEmail(emailRequest, emailRequestEntity);
        } catch (Exception ex) {
            log.error("Unknown error while sending email request [{}]: {}", emailRequestEntity.getTemplateCode(),
                    ex.getMessage());
            handleError(emailRequestEntity, ex);
        }
    }

    private void handleError(EmailRequestEntity emailRequestEntity, Exception ex) {
        emailRequestEntity.setRequestStatus(EmailRequestStatus.ERROR);
        emailRequestEntity.setDetails(ex.getMessage());
        emailRequestRepository.save(emailRequestEntity);
    }
}
