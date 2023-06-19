package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentStep;
import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.experiment.ExperimentContext;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

/**
 * Step handler to get experiment download url.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class GetExperimentDownloadUrlStepHandler extends AbstractExperimentStepHandler {

    private final AppProperties appProperties;
    private final ObjectStorageService objectStorageService;
    private final ExperimentStepService experimentStepService;
    private final ExperimentRepository experimentRepository;

    /**
     * Constructor with parameters.
     *
     * @param appProperties         - app properties
     * @param objectStorageService  - object storage service
     * @param experimentStepService - experiment step service
     * @param experimentRepository  - experiment repository
     */
    public GetExperimentDownloadUrlStepHandler(AppProperties appProperties,
                                               ObjectStorageService objectStorageService,
                                               ExperimentStepService experimentStepService,
                                               ExperimentRepository experimentRepository) {
        super(ExperimentStep.GET_EXPERIMENT_DOWNLOAD_URL);
        this.appProperties = appProperties;
        this.objectStorageService = objectStorageService;
        this.experimentStepService = experimentStepService;
        this.experimentRepository = experimentRepository;
    }

    @Override
    public void handle(ExperimentContext experimentContext,
                       ExperimentStepEntity experimentStepEntity) {
        try {
            Experiment experiment = experimentContext.getExperiment();
            StopWatch stopWatch = experimentContext.getStopWatch();
            stopWatch.start(String.format("Gets experiment [%s] download url", experiment.getRequestId()));
            String experimentDownloadUrl = getExperimentDownloadPresignedUrl(experiment.getModelPath());
            stopWatch.stop();
            experiment.setExperimentDownloadUrl(experimentDownloadUrl);
            experimentRepository.save(experiment);
            experimentStepService.complete(experimentStepEntity);
        } catch (ObjectStorageException ex) {
            log.error("Object storage error while get experiment [{}] download url: {}",
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
                        .expirationTime(appProperties.getModelDownloadUrlExpirationDays())
                        .expirationTimeUnit(TimeUnit.DAYS)
                        .build()
        );
    }
}
