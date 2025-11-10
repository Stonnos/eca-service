package com.ecaservice.server.service;

import com.ecaservice.s3.client.minio.service.MinioStorageService;
import com.ecaservice.server.model.EvaluationAttachmentData;
import com.ecaservice.server.model.entity.EvaluationResultsAttachmentType;
import com.ecaservice.server.repository.EvaluationResultsAttachmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EvaluationResultsAttachmentServiceTest} class.
 *
 * @author Roman Batygin
 */
@Import(EvaluationResultsAttachmentService.class)
class EvaluationResultsAttachmentServiceTest extends AbstractJpaTest {

    private static final String FILE_PATH = "file.png";
    private static final String ATTACHMENT_KEY = "key";

    @Autowired
    private EvaluationResultsAttachmentRepository evaluationResultsAttachmentRepository;

    @Autowired
    private EvaluationResultsAttachmentService evaluationResultsAttachmentService;

    @MockBean
    private MinioStorageService minioStorageService;

    @Override
    public void deleteAll() {
        evaluationResultsAttachmentRepository.deleteAll();
    }

    @Test
    void testSaveAttachment() {
        var evaluationAttachmentData = EvaluationAttachmentData.builder()
                .key(ATTACHMENT_KEY)
                .attachmentType(EvaluationResultsAttachmentType.ROC_CURVE_IMAGE)
                .filePath(FILE_PATH)
                .build();
        var file = new MockMultipartFile(FILE_PATH, FILE_PATH, MediaType.IMAGE_PNG_VALUE, new byte[0]);
        evaluationResultsAttachmentService.saveAttachment(file, evaluationAttachmentData);

        var attachments = evaluationResultsAttachmentRepository.findAll();
        assertThat(attachments).hasSize(1);
        var attachment = attachments.getFirst();
        assertThat(attachment.getAttachmentType()).isEqualTo(evaluationAttachmentData.getAttachmentType());
        assertThat(attachment.getKey()).isEqualTo(evaluationAttachmentData.getKey());
        assertThat(attachment.getFilePath()).isEqualTo(evaluationAttachmentData.getFilePath());
        assertThat(attachment.getCreatedDate()).isNotNull();
        assertThat(attachment.getUpdatedDate()).isNotNull();
    }

    @Test
    void testGetAttachment() {
        var evaluationAttachmentData = EvaluationAttachmentData.builder()
                .key(ATTACHMENT_KEY)
                .attachmentType(EvaluationResultsAttachmentType.ROC_CURVE_IMAGE)
                .filePath(FILE_PATH)
                .build();
        var file = new MockMultipartFile(FILE_PATH, FILE_PATH, MediaType.IMAGE_PNG_VALUE, new byte[0]);
        evaluationResultsAttachmentService.saveAttachment(file, evaluationAttachmentData);

        when(minioStorageService.downloadObject(anyString())).thenReturn(new ByteArrayInputStream(new byte[0]));

        byte[] content = evaluationResultsAttachmentService.getAttachment(evaluationAttachmentData.getKey(),
                evaluationAttachmentData.getAttachmentType());
        assertThat(content).isNotNull();
    }
}
