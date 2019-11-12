package com.ecaservice.service.experiment;

import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.async.AsyncTaskService;
import com.ecaservice.service.experiment.mail.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final NotificationService notificationService;
    private final AsyncTaskService asyncTaskService;

    /**
     * Creates experiment and send email notification.
     *
     * @param experimentRequest - experiment request
     * @return experiment entity
     */
    public Experiment createExperimentRequest(ExperimentRequest experimentRequest) {
        log.info("Received experiment request for data '{}', email '{}'", experimentRequest.getData().relationName(),
                experimentRequest.getEmail());
        Experiment experiment = experimentService.createExperiment(experimentRequest);
        asyncTaskService.perform(() -> {
            try {
                notificationService.notifyByEmail(experiment);
            } catch (Exception ex) {
                log.error("There was an error while sending email request for experiment [{}]: {}",
                        experiment.getUuid(), ex.getMessage());
            }
        });
        log.info("Experiment request [{}] has been created.", experiment.getUuid());
        return experiment;
    }
}
