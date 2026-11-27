package com.ecaservice.server.service.evaluation;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.model.EvaluationAttachmentData;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationResultsAttachmentType;
import com.ecaservice.server.repository.EvaluationLogRepository;
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

import static com.ecaservice.server.util.EvaluationResultsAttachmentKeys.EVALUATION_KEY_FORMAT;
import static com.ecaservice.server.util.S3ObjectPaths.EVALUATION_RESULTS_ATTACHMENT_PATH_FORMAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link SimpleEvaluationResultsAttachmentUploader} class.
 *
 * @author Roman Batygin
 */
@Import(SimpleEvaluationResultsAttachmentUploader.class)
class SimpleEvaluationResultsAttachmentUploaderTest extends AbstractJpaTest {

    private static final String FILE_PARAM_NAME = "file";
    private static final String ORIGINAL_FILE_NAME = "chart.png";
    private static final String EXPECTED_ATTACHMENT_FILE_NAME = "roc-curve-image.png";
    private static final byte[] FILE_CONTENT = "sample-image-bytes".getBytes();
    private static final Long NON_EXISTENT_MODEL_ID = -1L;

    @Autowired
    private EvaluationLogRepository evaluationLogRepository;

    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @MockBean
    private EvaluationResultsAttachmentService evaluationResultsAttachmentService;

    @Autowired
    private SimpleEvaluationResultsAttachmentUploader attachmentUploader;

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testUploadAttachmentSuccess() {
        EvaluationLog evaluationLog = saveEvaluationLog();
        MockMultipartFile file = new MockMultipartFile(
                FILE_PARAM_NAME,
                ORIGINAL_FILE_NAME,
                MediaType.IMAGE_PNG_VALUE,
                FILE_CONTENT
        );
        attachmentUploader.uploadAttachment(
                evaluationLog.getId(),
                file,
                EvaluationResultsAttachmentType.ROC_CURVE_IMAGE
        );

        ArgumentCaptor<EvaluationAttachmentData> dataCaptor =
                ArgumentCaptor.forClass(EvaluationAttachmentData.class);
        verify(evaluationResultsAttachmentService).saveAttachment(eq(file), dataCaptor.capture());

        EvaluationAttachmentData capturedData = dataCaptor.getValue();
        assertThat(capturedData).isNotNull();
        assertThat(capturedData.getKey())
                .isEqualTo(String.format(EVALUATION_KEY_FORMAT, evaluationLog.getRequestId()));
        assertThat(capturedData.getAttachmentType())
                .isEqualTo(EvaluationResultsAttachmentType.ROC_CURVE_IMAGE);
        assertThat(capturedData.getFilePath())
                .isEqualTo(String.format(
                        EVALUATION_RESULTS_ATTACHMENT_PATH_FORMAT,
                        evaluationLog.getRequestId(),
                        EXPECTED_ATTACHMENT_FILE_NAME
                ));
    }

    @Test
    void testUploadAttachmentWhenEvaluationLogNotFound() {
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

    private EvaluationLog saveEvaluationLog() {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        instancesInfoRepository.save(evaluationLog.getInstancesInfo());
        return evaluationLogRepository.save(evaluationLog);
    }
}
