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

import java.io.IOException;

@Slf4j
@Component
public class UploadExperimentModelStepHandler extends AbstractExperimentStepHandler {

    private static final String EXPERIMENT_PATH_FORMAT = "experiment-%s.model";

    private final ObjectStorageService objectStorageService;
    private final ExperimentStepService experimentStepService;
    private final ExperimentModelLocalStorage experimentModelLocalStorage;
    private final ExperimentRepository experimentRepository;

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
            log.info("Starting to get experiment history [{}] to upload", experiment.getRequestId());
            if (experimentContext.getExperimentHistory() == null) {
                log.info("Starting to get experiment history [{}] from local storage", experiment.getRequestId());
                var experimentHistory = experimentModelLocalStorage.get(experiment.getRequestId());
                experimentContext.setExperimentHistory(experimentHistory);
                log.info("Experiment history [{}] has been fetched from local storage", experiment.getRequestId());
                uploadObject(experiment, experimentHistory);
                experimentModelLocalStorage.delete(experiment.getRequestId());
            } else {
                log.info("Experiment history [{}] has been fetched from context", experiment.getRequestId());
                uploadObject(experiment, experimentContext.getExperimentHistory());
            }
            experimentStepService.complete(experimentStepEntity);
        } catch (ObjectStorageException ex) {
            log.error("Error while upload experiment [{}] model: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            saveModelToLocalStorage(experimentContext.getExperimentHistory(), experimentStepEntity);
            experimentStepService.failed(experimentStepEntity, ex.getMessage());
        } catch (Exception ex) {
            log.error("Error while upload experiment [{}] model: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.completeWithError(experimentStepEntity, ex.getMessage());
        }
    }

    private void uploadObject(Experiment experiment, AbstractExperiment<?> abstractExperiment) throws IOException {
        String experimentPath = String.format(EXPERIMENT_PATH_FORMAT, experiment.getRequestId());
        objectStorageService.uploadObject(abstractExperiment, experimentPath);
        experiment.setExperimentPath(experimentPath);
        experimentRepository.save(experiment);
    }

    private void saveModelToLocalStorage(AbstractExperiment<?> experimentHistory,
                                         ExperimentStepEntity experimentStepEntity) {
        try {
            experimentModelLocalStorage.saveIfAbsent(experimentStepEntity.getExperiment().getRequestId(),
                    experimentHistory);
        } catch (IOException ex) {
            log.error("I/O Error while save experiment [{}] model to local storage: {}",
                    experimentStepEntity.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.completeWithError(experimentStepEntity, ex.getMessage());
        }
    }
}
