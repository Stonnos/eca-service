package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.model.UploadObject;
import com.ecaservice.s3.client.minio.service.MinioStorageService;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.service.experiment.ExperimentModelLocalStorage;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

/**
 * Unit tests for {@link GetExperimentDownloadUrlStepHandler} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, ExperimentStepService.class, UploadExperimentModelStepHandler.class})
class UploadExperimentModelStepHandlerTest extends AbstractStepHandlerTest {

    @MockBean
    private MinioStorageService minioStorageService;
    @MockBean
    private ExperimentModelLocalStorage experimentModelLocalStorage;

    @Inject
    private UploadExperimentModelStepHandler uploadExperimentModelStepHandler;

    @Test
    void testUploadToS3WithError() {
        doThrow(new RuntimeException())
                .when(minioStorageService)
                .uploadObject(any(UploadObject.class));
        testStep(uploadExperimentModelStepHandler::handle, ExperimentStepStatus.ERROR);
    }

    @Test
    void testUploadToS3Failed() throws IOException {
        doThrow(new ObjectStorageException(new RuntimeException()))
                .when(minioStorageService)
                .uploadObject(any(UploadObject.class));
        testStep(uploadExperimentModelStepHandler::handle, ExperimentStepStatus.FAILED);
    }

    @Test
    void testUploadToS3Success() {
        testStep(uploadExperimentModelStepHandler::handle, ExperimentStepStatus.COMPLETED);
    }
}
