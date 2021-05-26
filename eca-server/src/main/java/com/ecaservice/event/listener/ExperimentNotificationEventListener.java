package com.ecaservice.event.listener;

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
        experiment.getRequestStatus().handle(experimentEmailVisitor, experiment);
    }

    /**
     * Handles event to sent web push about experiment status change.
     *
     * @param experimentWebPushEvent - experiment web push event
     */
    @EventListener
    public void handleExperimentPushEvent(ExperimentWebPushEvent experimentWebPushEvent) {
        webPushService.sendWebPush(experimentWebPushEvent.getExperiment());
    }
}
