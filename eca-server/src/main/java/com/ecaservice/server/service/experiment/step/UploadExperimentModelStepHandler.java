package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.experiment.ExperimentModelLocalStorage;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import eca.dataminer.AbstractExperiment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.IOException;

/**
 * Step handler to upload experiment model to S3.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UploadExperimentModelStepHandler extends AbstractExperimentStepHandler {

    private static final String EXPERIMENT_PATH_FORMAT = "experiment-%s.model";

    private final ObjectStorageService objectStorageService;
    private final ExperimentStepService experimentStepService;
    private final ExperimentModelLocalStorage experimentModelLocalStorage;
    private final ExperimentRepository experimentRepository;

    /**
     * Constructor with parameters.
     *
     * @param objectStorageService        - object storage service
     * @param experimentStepService       - experiment step service
     * @param experimentModelLocalStorage - experiment model local storage
     * @param experimentRepository        - experiment repository
     */
    public UploadExperimentModelStepHandler(ObjectStorageService objectStorageService,
                                            ExperimentStepService experimentStepService,
                                            ExperimentModelLocalStorage experimentModelLocalStorage,
                                            ExperimentRepository experimentRepository) {
        super(ExperimentStep.UPLOAD_EXPERIMENT_MODEL);
        this.objectStorageService = objectStorageService;
        this.experimentStepService = experimentStepService;
        this.experimentModelLocalStorage = experimentModelLocalStorage;
        this.experimentRepository = experimentRepository;
    }

    @Override
    public void handle(ExperimentContext experimentContext,
                       ExperimentStepEntity experimentStepEntity) {
        try {
            Experiment experiment = experimentContext.getExperiment();
            StopWatch stopWatch = experimentContext.getStopWatch();
            log.info("Starting to get experiment history [{}] to upload", experiment.getRequestId());
            if (experimentContext.getExperimentHistory() == null) {
                log.info("Starting to get experiment history [{}] from local storage", experiment.getRequestId());
                stopWatch.start(String.format("Load experiment history [%s] from local storage", experiment.getRequestId()));
                var experimentHistory = experimentModelLocalStorage.get(experiment.getRequestId());
                stopWatch.stop();
                experimentContext.setExperimentHistory(experimentHistory);
                log.info("Experiment history [{}] has been fetched from local storage", experiment.getRequestId());
                uploadObject(experiment, stopWatch, experimentHistory);
                experimentModelLocalStorage.delete(experiment.getRequestId());
            } else {
                log.info("Experiment history [{}] has been fetched from context", experiment.getRequestId());
                uploadObject(experiment, stopWatch, experimentContext.getExperimentHistory());
            }
            experimentStepService.complete(experimentStepEntity);
        } catch (ObjectStorageException ex) {
            log.error("Object storage error while upload experiment [{}] model: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            handleFailedUpload(experimentContext.getExperimentHistory(), experimentStepEntity, ex.getMessage());
        } catch (Exception ex) {
            log.error("Error while upload experiment [{}] model: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.completeWithError(experimentStepEntity, ex.getMessage());
        }
    }

    private void uploadObject(Experiment experiment,
                              StopWatch stopWatch,
                              AbstractExperiment<?> abstractExperiment) throws IOException {
        String experimentPath = String.format(EXPERIMENT_PATH_FORMAT, experiment.getRequestId());
        stopWatch.start(String.format("Uploads experiment history [%s] to S3", experiment.getRequestId()));
        objectStorageService.uploadObject(abstractExperiment, experimentPath);
        stopWatch.stop();
        experiment.setModelPath(experimentPath);
        experimentRepository.save(experiment);
    }

    private void handleFailedUpload(AbstractExperiment<?> experimentHistory,
                                    ExperimentStepEntity experimentStepEntity,
                                    String errorMessage) {
        try {
            experimentModelLocalStorage.saveIfAbsent(experimentStepEntity.getExperiment().getRequestId(),
                    experimentHistory);
            experimentStepService.failed(experimentStepEntity, errorMessage);
        } catch (Exception ex) {
            log.error("Error while save experiment [{}] model to local storage: {}",
                    experimentStepEntity.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.completeWithError(experimentStepEntity, ex.getMessage());
        }
    }
}
