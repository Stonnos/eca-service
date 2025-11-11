package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.model.UploadObject;
import com.ecaservice.s3.client.minio.service.MinioStorageService;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.service.experiment.ExperimentModelLocalStorage;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.List;

import static com.ecaservice.server.util.FileKeys.EXPERIMENT_RESULTS_AUC_PREDICTIONS;
import static com.ecaservice.server.util.S3ObjectPaths.EXPERIMENT_RESULTS_AUC_PREDICTIONS_PATH_FORMAT;

/**
 * Step handler to upload experiment AUC predictions data to S3.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class UploadExperimentAucPredictionsStepHandler extends AbstractExperimentStepHandler {

    private final MinioStorageService minioStorageService;
    private final ExperimentStepService experimentStepService;
    private final ExperimentModelLocalStorage experimentModelLocalStorage;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;

    /**
     * Constructor with parameters.
     *
     * @param minioStorageService               - minio storage service
     * @param experimentStepService             - experiment step service
     * @param experimentModelLocalStorage       - experiment model local storage
     * @param experimentResultsEntityRepository - experiment results entity repository
     */
    public UploadExperimentAucPredictionsStepHandler(MinioStorageService minioStorageService,
                                                     ExperimentStepService experimentStepService,
                                                     ExperimentModelLocalStorage experimentModelLocalStorage,
                                                     ExperimentResultsEntityRepository experimentResultsEntityRepository) {
        super(ExperimentStep.UPLOAD_AUC_PREDICTIONS);
        this.minioStorageService = minioStorageService;
        this.experimentStepService = experimentStepService;
        this.experimentModelLocalStorage = experimentModelLocalStorage;
        this.experimentResultsEntityRepository = experimentResultsEntityRepository;
    }

    @NewSpan("uploadExperimentAucPredictionsStep")
    @Override
    public void handle(ExperimentContext experimentContext,
                       ExperimentStepEntity experimentStepEntity) {
        try {
            experimentStepService.start(experimentStepEntity);
            Experiment experiment = experimentContext.getExperiment();
            var experimentResultsList =
                    experimentResultsEntityRepository.findByExperimentOrderByResultsIndex(experiment);
            StopWatch stopWatch = experimentContext.getStopWatch();
            stopWatch.start(String.format("Uploads experiment [%s] auc predictions  to S3", experiment.getRequestId()));
            experimentResultsList.forEach(this::uploadPredictions);
            stopWatch.stop();
            experimentResultsEntityRepository.saveAll(experimentResultsList);
            experimentStepService.complete(experimentStepEntity);
            deleteTempDataFiles(experimentResultsList);
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

    private void deleteTempDataFiles(List<ExperimentResultsEntity> experimentResultsList) {
        experimentResultsList.forEach(experimentResultsEntity -> {
            String fileName = String.format(EXPERIMENT_RESULTS_AUC_PREDICTIONS,
                    experimentResultsEntity.getExperiment().getRequestId(), experimentResultsEntity.getResultsIndex());
            experimentModelLocalStorage.deleteDataFile(fileName);
        });
    }

    private void uploadPredictions(ExperimentResultsEntity experimentResultsEntity) {
        String requestId = experimentResultsEntity.getExperiment().getRequestId();
        String predictionsPath = String.format(EXPERIMENT_RESULTS_AUC_PREDICTIONS_PATH_FORMAT,
                requestId, requestId, experimentResultsEntity.getId());
        String fileName =
                String.format(EXPERIMENT_RESULTS_AUC_PREDICTIONS, requestId, experimentResultsEntity.getResultsIndex());
        minioStorageService.uploadObject(
                UploadObject.builder()
                        .objectPath(predictionsPath)
                        .inputStream(() -> experimentModelLocalStorage.getDataFileInputStream(fileName))
                        .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .build()
        );
        experimentResultsEntity.setAucPredictionsPath(predictionsPath);
    }
}
