package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
public class UploadExperimentModelStepHandler extends AbstractExperimentStepHandler {

    private static final String EXPERIMENT_PATH_FORMAT = "experiment-%s.model";

    private final ObjectStorageService objectStorageService;
    private final ExperimentStepService experimentStepService;
    private final ExperimentRepository experimentRepository;

    public UploadExperimentModelStepHandler(ObjectStorageService objectStorageService,
                                            ExperimentStepService experimentStepService,
                                            ExperimentRepository experimentRepository) {
        super(ExperimentStep.EXPERIMENT_PROCESSING);
        this.objectStorageService = objectStorageService;
        this.experimentStepService = experimentStepService;
        this.experimentRepository = experimentRepository;
    }

    @Override
    public void handle(ExperimentContext experimentContext,
                       ExperimentStepEntity experimentStepEntity) throws Exception {
        try {
            Experiment experiment = experimentContext.getExperiment();
            var abstractExperiment = experimentContext.getExperimentHistory();
            Assert.notNull(abstractExperiment,
                    String.format("Expected not null experiment [%s] history to upload", experiment.getRequestId()));
            String experimentPath = String.format(EXPERIMENT_PATH_FORMAT, experiment.getRequestId());
            objectStorageService.uploadObject(abstractExperiment, experimentPath);
            experiment.setExperimentPath(experimentPath);
            experimentRepository.save(experiment);
            experimentStepService.complete(experimentStepEntity);
        } catch (ObjectStorageException ex) {
            experimentStepService.failed(experimentStepEntity, ex.getMessage());
        } catch (Exception ex) {
            experimentStepService.completeWithError(experimentStepEntity, ex.getMessage());
            throw ex;
        }
    }
}
