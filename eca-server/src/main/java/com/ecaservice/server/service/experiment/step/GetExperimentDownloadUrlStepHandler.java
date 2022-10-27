package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Step handler to get experiment download url.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class GetExperimentDownloadUrlStepHandler extends AbstractExperimentStepHandler {

    private final ExperimentConfig experimentConfig;
    private final ObjectStorageService objectStorageService;
    private final ExperimentStepService experimentStepService;
    private final ExperimentRepository experimentRepository;

    /**
     * Constructor with parameters.
     *
     * @param experimentConfig      - experiment config
     * @param objectStorageService  - object storage service
     * @param experimentStepService - experiment step service
     * @param experimentRepository  - experiment repository
     */
    public GetExperimentDownloadUrlStepHandler(ExperimentConfig experimentConfig,
                                               ObjectStorageService objectStorageService,
                                               ExperimentStepService experimentStepService,
                                               ExperimentRepository experimentRepository) {
        super(ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL);
        this.experimentConfig = experimentConfig;
        this.objectStorageService = objectStorageService;
        this.experimentStepService = experimentStepService;
        this.experimentRepository = experimentRepository;
    }

    @Override
    public void handle(ExperimentContext experimentContext,
                       ExperimentStepEntity experimentStepEntity) {
        try {
            Experiment experiment = experimentContext.getExperiment();
            String experimentDownloadUrl = getExperimentDownloadPresignedUrl(experiment.getExperimentPath());
            experiment.setExperimentDownloadUrl(experimentDownloadUrl);
            experimentRepository.save(experiment);
            experimentStepService.complete(experimentStepEntity);
        } catch (ObjectStorageException ex) {
            log.error("Error while get experiment [{}] download url: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.failed(experimentStepEntity, ex.getMessage());
        } catch (Exception ex) {
            log.error("Error while get experiment [{}] download url: {}",
                    experimentContext.getExperiment().getRequestId(), ex.getMessage());
            experimentStepService.completeWithError(experimentStepEntity, ex.getMessage());
        }
    }

    private String getExperimentDownloadPresignedUrl(String experimentPath) {
        return objectStorageService.getObjectPresignedProxyUrl(
                GetPresignedUrlObject.builder()
                        .objectPath(experimentPath)
                        .expirationTime(experimentConfig.getExperimentDownloadUrlExpirationDays())
                        .expirationTimeUnit(TimeUnit.DAYS)
                        .build()
        );
    }
}
