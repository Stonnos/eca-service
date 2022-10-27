package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.event.model.ExperimentFinishedEvent;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import eca.dataminer.AbstractExperiment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class ExperimentErsReportProcessorStepHandler extends AbstractExperimentStepHandler {

    private final ObjectStorageService objectStorageService;
    private final ExperimentStepService experimentStepService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ExperimentErsReportProcessorStepHandler(ObjectStorageService objectStorageService,
                                                   ExperimentStepService experimentStepService,
                                                   ApplicationEventPublisher applicationEventPublisher) {
        super(ExperimentStep.CREATE_ERS_REPORT);
        this.objectStorageService = objectStorageService;
        this.experimentStepService = experimentStepService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void handle(ExperimentContext experimentContext,
                       ExperimentStepEntity experimentStepEntity) {
        try {
            fetchExperimentHistory(experimentContext);
            applicationEventPublisher.publishEvent(new ExperimentFinishedEvent(this,
                    experimentContext.getExperiment(), experimentContext.getExperimentHistory()));
            experimentStepService.complete(experimentStepEntity);
        } catch (ObjectStorageException ex) {
            experimentStepService.failed(experimentStepEntity, ex.getMessage());
        } catch (Exception ex) {
            experimentStepService.completeWithError(experimentStepEntity, ex.getMessage());
        }
    }

    private void fetchExperimentHistory(ExperimentContext experimentContext)
            throws IOException, ClassNotFoundException {
        var experiment = experimentContext.getExperiment();
        log.info("Starting to get experiment history [{}] for ers report", experiment.getRequestId());
        if (experimentContext.getExperimentHistory() != null) {
            log.info("Experiment history [{}] has been fetched from context", experiment.getRequestId());
        } else {
            log.info("Starting to get experiment history [{}] from local storage", experiment.getRequestId());
            var experimentHistory =
                    objectStorageService.getObject(experiment.getExperimentPath(), AbstractExperiment.class);
            log.info("Experiment history [{}] has been fetched from local storage", experiment.getRequestId());
            experimentContext.setExperimentHistory(experimentHistory);
        }
    }
}
