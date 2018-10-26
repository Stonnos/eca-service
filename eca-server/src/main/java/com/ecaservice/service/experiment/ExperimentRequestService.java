package com.ecaservice.service.experiment;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.mapping.EcaResponseMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.service.async.AsyncTaskService;
import com.ecaservice.service.experiment.mail.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;

import static com.ecaservice.util.Utils.buildErrorResponse;
import static com.ecaservice.util.Utils.isValidEmail;

/**
 * Experiment request service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentRequestService {

    private final ExperimentService experimentService;
    private final NotificationService notificationService;
    private final AsyncTaskService asyncTaskService;
    private final EcaResponseMapper ecaResponseMapper;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentService   - experiment service bean
     * @param notificationService - notification service bean
     * @param asyncTaskService    - async task service bean
     * @param ecaResponseMapper   - eca response mapper bean
     */
    @Inject
    public ExperimentRequestService(ExperimentService experimentService,
                                    NotificationService notificationService,
                                    AsyncTaskService asyncTaskService,
                                    EcaResponseMapper ecaResponseMapper) {
        this.experimentService = experimentService;
        this.notificationService = notificationService;
        this.asyncTaskService = asyncTaskService;
        this.ecaResponseMapper = ecaResponseMapper;
    }

    /**
     * Creates experiment and save email with experiment unique uuid.
     *
     * @param experimentRequest - experiment request
     * @return eca response
     */
    public EcaResponse createExperimentRequest(ExperimentRequest experimentRequest) {
        Assert.notNull(experimentRequest, "Experiment request is not specified!");
        log.info("Received experiment request for data '{}', email '{}'", experimentRequest.getData().relationName(),
                experimentRequest.getEmail());
        if (!isValidEmail(experimentRequest.getEmail())) {
            return buildErrorResponse("Invalid email!");
        }
        Experiment experiment = experimentService.createExperiment(experimentRequest);
        asyncTaskService.perform(() -> {
            try {
                notificationService.notifyByEmail(experiment);
            } catch (Exception ex) {
                log.error("There was an error while sending email request for experiment with id [{}]: {}",
                        experiment.getId(), ex.getMessage());
            }
        });
        EcaResponse ecaResponse = ecaResponseMapper.map(experiment);
        log.info("Experiment request [{}] has been created with status [{}].", ecaResponse.getRequestId(),
                ecaResponse.getStatus());
        return ecaResponse;
    }
}
