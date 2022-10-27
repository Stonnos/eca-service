package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GetExperimentDownloadUrlStepHandler} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, ExperimentStepService.class, GetExperimentDownloadUrlStepHandler.class})
class GetExperimentDownloadStepHandlerTest extends AbstractStepHandlerTest {

    private static final String EXPERIMENT_DOWNLOAD_URL = "http://localhost:8099/object-storage";

    @MockBean
    private ObjectStorageService objectStorageService;

    @Inject
    private GetExperimentDownloadUrlStepHandler getExperimentDownloadUrlStepHandler;

    @Test
    void testGetDownloadUrlWithError() {
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenThrow(new RuntimeException());
        testStep(getExperimentDownloadUrlStepHandler::handle, ExperimentStepStatus.ERROR);
    }

    @Test
    void testGetDownloadUrlFailed() {
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenThrow(new ObjectStorageException(new RuntimeException()));
        testStep(getExperimentDownloadUrlStepHandler::handle, ExperimentStepStatus.FAILED);
    }

    @Test
    void testGetDownloadUrlSuccess() {
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenReturn(EXPERIMENT_DOWNLOAD_URL);
        testStep(getExperimentDownloadUrlStepHandler::handle, ExperimentStepStatus.COMPLETED);
        var experiment =
                getExperimentRepository().findById(getExperimentStepEntity().getExperiment().getId()).orElse(null);
        assertThat(experiment).isNotNull();
        assertThat(experiment.getExperimentDownloadUrl()).isEqualTo(EXPERIMENT_DOWNLOAD_URL);
    }
}
