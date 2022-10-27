package com.ecaservice.server.service.experiment.step;

import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.service.experiment.ExperimentModelLocalStorage;
import com.ecaservice.server.service.experiment.ExperimentStepService;
import eca.dataminer.AbstractExperiment;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;

import static com.ecaservice.server.TestHelperUtils.createExperimentHistory;
import static com.ecaservice.server.TestHelperUtils.loadInstances;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GetExperimentDownloadUrlStepHandler} class.
 *
 * @author Roman Batygin
 */
@Import({ExperimentConfig.class, ExperimentStepService.class, UploadExperimentModelStepHandler.class})
class UploadExperimentModelStepHandlerTest extends AbstractStepHandlerTest {

    @MockBean
    private ObjectStorageService objectStorageService;
    @MockBean
    private ExperimentModelLocalStorage experimentModelLocalStorage;

    @Inject
    private UploadExperimentModelStepHandler uploadExperimentModelStepHandler;

    private AbstractExperiment experimentHistory;

    @Override
    public void init() {
        super.init();
        var data = loadInstances();
        experimentHistory = createExperimentHistory(data);
    }

    @Test
    void testUploadToS3WithError() throws IOException {
        doThrow(new RuntimeException())
                .when(objectStorageService)
                .uploadObject(any(Serializable.class), anyString());
        when(experimentModelLocalStorage.get(anyString())).thenReturn(experimentHistory);
        testStep(uploadExperimentModelStepHandler::handle, ExperimentStepStatus.ERROR);
    }

    @Test
    void testUploadToS3Failed() throws IOException {
        doThrow(new ObjectStorageException(new RuntimeException()))
                .when(objectStorageService)
                .uploadObject(any(Serializable.class), anyString());
        when(experimentModelLocalStorage.get(anyString())).thenReturn(experimentHistory);
        testStep(uploadExperimentModelStepHandler::handle, ExperimentStepStatus.FAILED);
        verify(experimentModelLocalStorage, atLeastOnce()).saveIfAbsent(anyString(), any(AbstractExperiment.class));
    }

    @Test
    void testUploadToS3FailedAndLocalStorageError() throws IOException {
        doThrow(new ObjectStorageException(new RuntimeException()))
                .when(objectStorageService)
                .uploadObject(any(Serializable.class), anyString());
        when(experimentModelLocalStorage.get(anyString())).thenReturn(experimentHistory);
        doThrow(new RuntimeException()).when(experimentModelLocalStorage).saveIfAbsent(anyString(),
                any(AbstractExperiment.class));
        testStep(uploadExperimentModelStepHandler::handle, ExperimentStepStatus.ERROR);
    }

    @Test
    void testUploadToS3Success() throws IOException {
        when(experimentModelLocalStorage.get(anyString())).thenReturn(experimentHistory);
        testStep(uploadExperimentModelStepHandler::handle, ExperimentStepStatus.COMPLETED);
    }
}
