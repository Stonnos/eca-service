package com.ecaservice.server.event.listener;

import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.service.experiment.mail.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Experiment notification event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentNotificationEventListener {

    private final NotificationService notificationService;

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
        notificationService.notifyByEmail(experiment);
    }
}
