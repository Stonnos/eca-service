package com.ecaservice.mail.scheduler;

import com.ecaservice.mail.metrics.MetricsService;
import com.ecaservice.mail.service.MailSenderService;
import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.model.EmailStatus;
import com.ecaservice.mail.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.mail.model.Email_.PRIORITY;
import static com.ecaservice.mail.model.Email_.SAVE_DATE;

/**
 * Email scheduler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailScheduler {

    private final MailConfig mailConfig;
    private final MetricsService metricsService;
    private final MailSenderService mailSenderService;
    private final EmailRepository emailRepository;

    /**
     * Processes not sent emails.
     */
    @Scheduled(fixedDelayString = "${mail-config.delaySeconds}000")
    public void sendEmails() {
        Sort sort = Sort.by(Sort.Order.desc(PRIORITY), Sort.Order.asc(SAVE_DATE));
        List<Email> emails = emailRepository.findByStatusNotIn(Arrays.asList(EmailStatus.SENT, EmailStatus.EXCEEDED),
                PageRequest.of(0, mailConfig.getPageSize(), sort));
        log.trace("{} not sent emails has been found.", emails.size());
        for (Email email : emails) {
            putMdc(TX_ID, email.getTxId());
            try {
                mailSenderService.sendEmail(email);
                email.setStatus(EmailStatus.SENT);
                email.setSentDate(LocalDateTime.now());
                metricsService.trackSendingEmailMessageSuccessTotalCounter();
            } catch (Exception ex) {
                log.error("There was an error while sending email [{}]: {} ", email.getId(), ex.getMessage());
                metricsService.trackSendingEmailMessageErrorTotalCounter();
                handleErrorSent(email, ex.getMessage());
            } finally {
                emailRepository.save(email);
            }
        }
        log.trace("Email sending has been finished.");
    }

    private void handleErrorSent(Email email, String errorMessage) {
        int failedAttemptsToSent = email.getFailedAttemptsToSent() + 1;
        if (failedAttemptsToSent >= mailConfig.getMaxFailedAttemptsToSent()) {
            email.setStatus(EmailStatus.EXCEEDED);
        } else {
            email.setStatus(EmailStatus.NOT_SENT);
            email.setErrorMessage(errorMessage);
        }
        email.setFailedAttemptsToSent(failedAttemptsToSent);
    }
}
