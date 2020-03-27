package com.ecaservice.event.listener;

import com.ecaservice.event.model.ExperimentCreatedEvent;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.experiment.mail.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.ecaservice.config.EcaServiceConfiguration.ECA_THREAD_POOL_TASK_EXECUTOR;

/**
 * Event listener that occurs after experiment is created.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentCreatedEventListener {

    private final NotificationService notificationService;

    /**
     * Handles event to sent email about experiment request creation.
     *
     * @param experimentCreatedEvent - experiment created event
     */
    @Async(ECA_THREAD_POOL_TASK_EXECUTOR)
    @EventListener
    public void handleExperimentCreatedEvent(ExperimentCreatedEvent experimentCreatedEvent) {
        Experiment experiment = experimentCreatedEvent.getExperiment();
        try {
            notificationService.notifyByEmail(experiment);
        } catch (Exception ex) {
            log.error("There was an error while sending email request for experiment [{}]: {}",
                    experiment.getRequestId(), ex.getMessage());
        }
    }
}
