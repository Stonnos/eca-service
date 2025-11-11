package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.model.UploadObject;
import com.ecaservice.s3.client.minio.service.MinioStorageService;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.experiment.ExperimentModelLocalStorage;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import static com.ecaservice.server.util.FileKeys.EXPERIMENT_MODEL_ZIP;
import static com.ecaservice.server.util.S3ObjectPaths.EXPERIMENT_PATH_FORMAT;

/**
 * Step handler to upload experiment model to S3.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UploadExperimentModelStepHandler extends AbstractExperimentStepHandler {

    private final MinioStorageService minioStorageService;
    private final ExperimentStepService experimentStepService;
    private final ExperimentModelLocalStorage experimentModelLocalStorage;
    private final ExperimentRepository experimentRepository;

    /**
     * Constructor with parameters.
     *
     * @param minioStorageService         - minio storage service
     * @param experimentStepService       - experiment step service
     * @param experimentModelLocalStorage - experiment model local storage
     * @param experimentRepository        - experiment repository
     */
    public UploadExperimentModelStepHandler(MinioStorageService minioStorageService,
                                            ExperimentStepService experimentStepService,
                                            ExperimentModelLocalStorage experimentModelLocalStorage,
                                            ExperimentRepository experimentRepository) {
        super(ExperimentStep.UPLOAD_EXPERIMENT_MODEL);
        this.minioStorageService = minioStorageService;
        this.experimentStepService = experimentStepService;
        this.experimentModelLocalStorage = experimentModelLocalStorage;
        this.experimentRepository = experimentRepository;
    }

    @NewSpan("uploadExperimentModelStep")
    @Override
    public void handle(ExperimentContext experimentContext,
                       ExperimentStepEntity experimentStepEntity) {
        try {
            experimentStepService.start(experimentStepEntity);
            Experiment experiment = experimentContext.getExperiment();
            StopWatch stopWatch = experimentContext.getStopWatch();
            uploadObject(experiment, stopWatch);
            experimentStepService.complete(experimentStepEntity);
            deleteTempModelFile(experiment);
        } catch (ObjectStorageException ex) {
            log.error("Object storage error while upload experiment [{}] model: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage(), ex);
            experimentStepService.failed(experimentStepEntity, ex.getMessage());
        } catch (Exception ex) {
            log.error("Error while upload experiment [{}] model: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage(), ex);
            experimentStepService.completeWithError(experimentStepEntity, ex.getMessage());
        }
    }

    private void deleteTempModelFile(Experiment experiment) {
        String fileName = String.format(EXPERIMENT_MODEL_ZIP, experiment.getRequestId());
        experimentModelLocalStorage.deleteDataFile(fileName);
    }

    private void uploadObject(Experiment experiment, StopWatch stopWatch) {
        String experimentPath = String.format(EXPERIMENT_PATH_FORMAT, experiment.getRequestId());
        stopWatch.start(String.format("Uploads experiment history [%s] to S3", experiment.getRequestId()));
        String fileName = String.format(EXPERIMENT_MODEL_ZIP, experiment.getRequestId());
        minioStorageService.uploadObject(
                UploadObject.builder()
                        .objectPath(experimentPath)
                        .inputStream(() -> experimentModelLocalStorage.getDataFileInputStream(fileName))
                        .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .build()
        );
        stopWatch.stop();
        experiment.setModelPath(experimentPath);
        experimentRepository.save(experiment);
    }
}
