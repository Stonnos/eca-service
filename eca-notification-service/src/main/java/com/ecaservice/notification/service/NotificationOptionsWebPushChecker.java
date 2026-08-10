package com.ecaservice.notification.service;

import com.ecaservice.notification.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Notification options web push checker.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationOptionsWebPushChecker implements NotificationOptionsChecker {

    private final AppProperties appProperties;
    private final NotificationOptionsDataService notificationOptionsDataService;


    @Override
    public boolean isEnabled(String user, String messageCode) {
        var emailCodesMapping = appProperties.getWebPushCodesMapping()
                .stream()
                .filter(mapping -> messageCode.equals(mapping.getMessageCode()))
                .findFirst()
                .orElse(null);
        if (emailCodesMapping != null) {
            var notificationOptions = notificationOptionsDataService.getOrCreateOptions(user);
            return notificationOptions.isWebPushEnabled() && notificationOptions.getNotificationEventOptions()
                    .stream()
                    .anyMatch(event ->
                            event.getEventType().equals(emailCodesMapping.getEventType()) && event.isWebPushEnabled());
        }
        log.debug("Notification web push event mapping not found for user [{}], message code [{}]", user, messageCode);
        return true;
    }
}
