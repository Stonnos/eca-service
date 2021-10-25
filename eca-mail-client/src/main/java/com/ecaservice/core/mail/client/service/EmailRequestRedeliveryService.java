package com.ecaservice.core.mail.client.service;

import com.ecaservice.core.lock.annotation.TryLocked;
import com.ecaservice.core.mail.client.config.EcaMailClientProperties;
import com.ecaservice.core.mail.client.entity.EmailRequestEntity;
import com.ecaservice.core.mail.client.entity.EmailRequestStatus;
import com.ecaservice.core.mail.client.mapping.EmailRequestMapper;
import com.ecaservice.core.mail.client.repository.EmailRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static com.ecaservice.core.mail.client.config.EcaMailClientAutoConfiguration.MAIL_LOCK_REGISTRY;
import static com.ecaservice.core.mail.client.util.Utils.readVariables;

/**
 * Audit redelivery service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "mail.client.redelivery", havingValue = "true")
@RequiredArgsConstructor
public class EmailRequestRedeliveryService {

    private final EcaMailClientProperties ecaMailClientProperties;
    private final EmailRequestMapper emailRequestMapper;
    private final EmailRequestSender emailRequestSender;
    private final EmailRequestRepository emailRequestRepository;

    /**
     * Redeliver not sent email requests.
     */
    @TryLocked(lockName = "processNotSentEmailRequests", lockRegistry = MAIL_LOCK_REGISTRY)
    public void processNotSentEmailRequests() {
        log.debug("Starting redeliver email requests");
        var ids = emailRequestRepository.findNotSentEmailRequests(LocalDateTime.now());
        if (!CollectionUtils.isEmpty(ids)) {
            log.info("Found [{}] not sent email requests", ids.size());
            Pageable pageRequest = PageRequest.of(0, ecaMailClientProperties.getPageSize());
            Page<EmailRequestEntity> page;
            do {
                page = emailRequestRepository.findByIdIn(ids, pageRequest);
                if (page == null || !page.hasContent()) {
                    log.trace("No one email request has been fetched");
                    break;
                } else {
                    page.forEach(this::sendEmailRequest);
                }
                pageRequest = page.nextPageable();
            } while (page.hasNext());
        }
        log.debug("Redeliver email requests has been finished");
    }

    private void sendEmailRequest(EmailRequestEntity emailRequestEntity) {
        try {
            var emailRequest = emailRequestMapper.map(emailRequestEntity);
            var variables = getVariables(emailRequestEntity);
            emailRequest.setVariables(variables);
            emailRequestSender.sendEmail(emailRequest, emailRequestEntity);
        } catch (Exception ex) {
            log.error("Unknown error while sending email request [{}]: {}", emailRequestEntity.getTemplateCode(),
                    ex.getMessage());
            handleError(emailRequestEntity, ex);
        }
    }

    private Map<String, String> getVariables(EmailRequestEntity emailRequestEntity) {
        if (StringUtils.isEmpty(emailRequestEntity.getVariablesJson())) {
            return Collections.emptyMap();
        }
        return readVariables(emailRequestEntity.getVariablesJson());
    }

    private void handleError(EmailRequestEntity emailRequestEntity, Exception ex) {
        emailRequestEntity.setRequestStatus(EmailRequestStatus.ERROR);
        emailRequestEntity.setDetails(ex.getMessage());
        emailRequestRepository.save(emailRequestEntity);
    }
}
