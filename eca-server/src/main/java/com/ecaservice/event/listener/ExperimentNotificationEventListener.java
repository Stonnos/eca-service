package com.ecaservice.event.listener;

import com.ecaservice.config.AppProperties;
import com.ecaservice.event.model.ExperimentEmailEvent;
import com.ecaservice.event.model.ExperimentWebPushEvent;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.experiment.visitor.ExperimentEmailVisitor;
import com.ecaservice.service.push.WebPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener that occurs after experiment status is changed.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentNotificationEventListener {

    private final AppProperties appProperties;
    private final ExperimentEmailVisitor experimentEmailVisitor;
    private final WebPushService webPushService;

    /**
     * Handles event to sent email about experiment status change.
     *
     * @param experimentEmailEvent - experiment email event
     */
    @EventListener
    public void handleExperimentEmailEvent(ExperimentEmailEvent experimentEmailEvent) {
        Experiment experiment = experimentEmailEvent.getExperiment();
        log.info("Handles experiment [{}] email event from source [{}]", experiment.getRequestId(),
                experimentEmailEvent.getSource().getClass().getSimpleName());
        if (!Boolean.TRUE.equals(appProperties.getNotifications().getEmailsEnabled())) {
            log.warn("Emails sending are disabled. You may set [app.notifications.emailsEnabled] property");
        } else {
            experiment.getRequestStatus().handle(experimentEmailVisitor, experiment);
        }
    }

    /**
     * Handles event to sent web push about experiment status change.
     *
     * @param experimentWebPushEvent - experiment web push event
     */
    @EventListener
    public void handleExperimentPushEvent(ExperimentWebPushEvent experimentWebPushEvent) {
        Experiment experiment = experimentWebPushEvent.getExperiment();
        log.info("Handles experiment [{}] web push event from source [{}]", experiment.getRequestId(),
                experimentWebPushEvent.getSource().getClass().getSimpleName());
        if (!Boolean.TRUE.equals(appProperties.getNotifications().getWebPushesEnabled())) {
            log.warn("Web pushes are disabled. You may set [app.notifications.webPushesEnabled] property");
        } else {
            webPushService.sendWebPush(experimentWebPushEvent.getExperiment());
        }
    }
}
