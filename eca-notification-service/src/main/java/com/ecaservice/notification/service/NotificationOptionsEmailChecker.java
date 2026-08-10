package com.ecaservice.notification.service;

import com.ecaservice.notification.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Notification options email checker.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationOptionsEmailChecker implements NotificationOptionsChecker {

    private final AppProperties appProperties;
    private final NotificationOptionsDataService notificationOptionsDataService;

    @Override
    public boolean isEnabled(String user, String messageCode) {
        if (StringUtils.isEmpty(user)) {
            log.debug("Got empty user for message code [{}]", messageCode);
            return true;
        }
        var emailCodesMapping = appProperties.getEmailCodesMapping()
                .stream()
                .filter(mapping -> messageCode.equals(mapping.getMessageCode()))
                .findFirst()
                .orElse(null);
        if (emailCodesMapping != null) {
            var notificationOptions = notificationOptionsDataService.getOrCreateOptions(user);
            return notificationOptions.isEmailEnabled() && notificationOptions.getNotificationEventOptions()
                    .stream()
                    .anyMatch(event ->
                            event.getEventType().equals(emailCodesMapping.getEventType()) && event.isEmailEnabled());
        }
        log.debug("Notification email event mapping not found for user [{}], message code [{}]", user, messageCode);
        return true;
    }
}
