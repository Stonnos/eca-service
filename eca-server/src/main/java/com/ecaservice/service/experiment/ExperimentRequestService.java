package com.ecaservice.service.experiment;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.event.model.ExperimentNotificationEvent;
import com.ecaservice.model.entity.Experiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Experiment request service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentRequestService {

    private final ExperimentService experimentService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Creates experiment and send email notification.
     *
     * @param experimentRequest - experiment request
     * @return experiment entity
     */
    public Experiment createExperimentRequest(ExperimentRequest experimentRequest) {
        Experiment experiment = experimentService.createExperiment(experimentRequest);
        eventPublisher.publishEvent(new ExperimentNotificationEvent(this, experiment));
        log.info("Experiment request [{}] has been created.", experiment.getRequestId());
        return experiment;
    }
}
