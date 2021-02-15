package com.ecaservice.event.listener;

import com.ecaservice.event.model.ExperimentChangeStatusEvent;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.experiment.mail.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.ecaservice.config.EcaServiceConfiguration.ECA_THREAD_POOL_TASK_EXECUTOR;

/**
 * Event listener that occurs after experiment status is changed.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExperimentChangeStatusEventListener {

    private final NotificationService notificationService;

    /**
     * Handles event to sent email about experiment status change.
     *
     * @param changeStatusEvent - experiment change status event
     */
    @Async(ECA_THREAD_POOL_TASK_EXECUTOR)
    @EventListener
    public void handleChangeStatusEvent(ExperimentChangeStatusEvent changeStatusEvent) {
        Experiment experiment = changeStatusEvent.getExperiment();
        try {
            notificationService.notifyByEmail(experiment);
        } catch (Exception ex) {
            log.error("There was an error while sending email request for experiment [{}]: {}",
                    experiment.getRequestId(), ex.getMessage());
        }
    }
}
