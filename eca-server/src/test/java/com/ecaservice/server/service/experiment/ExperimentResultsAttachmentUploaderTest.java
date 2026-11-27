package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.model.EvaluationAttachmentData;
import com.ecaservice.server.model.entity.EvaluationResultsAttachmentType;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.EvaluationResultsAttachmentService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

import static com.ecaservice.server.util.EvaluationResultsAttachmentKeys.EXPERIMENT_RESULTS_KEY_FORMAT;
import static com.ecaservice.server.util.S3ObjectPaths.EXPERIMENT_RESULTS_ATTACHMENT_PATH_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link ExperimentResultsAttachmentUploader} class.
 *
 * @author Roman Batygin
 */
@Import(ExperimentResultsAttachmentUploader.class)
class ExperimentResultsAttachmentUploaderTest extends AbstractJpaTest {

    private static final String FILE_PARAM_NAME = "file";
    private static final String ORIGINAL_FILE_NAME = "chart.png";
    private static final String EXPECTED_ATTACHMENT_FILE_NAME_FORMAT = "roc-curve-image-%d.png";
    private static final byte[] FILE_CONTENT = "sample-image-bytes".getBytes();
    private static final Long NON_EXISTENT_MODEL_ID = -1L;

    @Autowired
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;

    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @MockBean
    private EvaluationResultsAttachmentService evaluationResultsAttachmentService;

    @Autowired
    private ExperimentResultsAttachmentUploader attachmentUploader;

    @Override
    public void deleteAll() {
        experimentResultsEntityRepository.deleteAll();
        experimentRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testUploadAttachmentSuccess() {
        ExperimentResultsEntity resultsEntity = saveExperimentResultsEntity();
        Experiment experiment = resultsEntity.getExperiment();
        MockMultipartFile file = new MockMultipartFile(
                FILE_PARAM_NAME,
                ORIGINAL_FILE_NAME,
                MediaType.IMAGE_PNG_VALUE,
                FILE_CONTENT
        );

        attachmentUploader.uploadAttachment(
                resultsEntity.getId(),
                file,
                EvaluationResultsAttachmentType.ROC_CURVE_IMAGE
        );

        ArgumentCaptor<EvaluationAttachmentData> dataCaptor =
                ArgumentCaptor.forClass(EvaluationAttachmentData.class);
        verify(evaluationResultsAttachmentService).saveAttachment(eq(file), dataCaptor.capture());

        String expectedFileName = String.format(EXPECTED_ATTACHMENT_FILE_NAME_FORMAT, resultsEntity.getId());
        EvaluationAttachmentData capturedData = dataCaptor.getValue();
        assertThat(capturedData).isNotNull();
        assertThat(capturedData.getKey())
                .isEqualTo(String.format(
                        EXPERIMENT_RESULTS_KEY_FORMAT,
                        experiment.getRequestId(),
                        resultsEntity.getId()
                ));
        assertThat(capturedData.getAttachmentType())
                .isEqualTo(EvaluationResultsAttachmentType.ROC_CURVE_IMAGE);
        assertThat(capturedData.getFilePath())
                .isEqualTo(String.format(
                        EXPERIMENT_RESULTS_ATTACHMENT_PATH_FORMAT,
                        experiment.getRequestId(),
                        expectedFileName
                ));
    }

    @Test
    void testUploadAttachmentWhenExperimentResultsNotFound() {
        MockMultipartFile file = new MockMultipartFile(
                FILE_PARAM_NAME,
                ORIGINAL_FILE_NAME,
                MediaType.IMAGE_PNG_VALUE,
                FILE_CONTENT
        );

        assertThatThrownBy(() -> attachmentUploader.uploadAttachment(
                NON_EXISTENT_MODEL_ID,
                file,
                EvaluationResultsAttachmentType.ROC_CURVE_IMAGE
        )).isInstanceOf(EntityNotFoundException.class);

        verifyNoInteractions(evaluationResultsAttachmentService);
    }

    private ExperimentResultsEntity saveExperimentResultsEntity() {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        ExperimentResultsEntity resultsEntity = TestHelperUtils.createExperimentResultsEntity(experiment);
        instancesInfoRepository.save(experiment.getInstancesInfo());
        experimentRepository.save(experiment);
        return experimentResultsEntityRepository.save(resultsEntity);
    }
}
