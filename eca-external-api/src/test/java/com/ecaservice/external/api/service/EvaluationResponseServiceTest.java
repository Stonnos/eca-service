package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.time.LocalDateTime;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static com.ecaservice.external.api.TestHelperUtils.errorEvaluationResponse;
import static com.ecaservice.external.api.TestHelperUtils.successEvaluationResponse;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EvaluationResponseService} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({ExternalApiConfig.class, EvaluationResponseService.class})
class EvaluationResponseServiceTest {

    private static final String MODEL_DOWNLOAD_URL_FORMAT = "%s/download-model/%s";

    @Inject
    private ExternalApiConfig externalApiConfig;
    @Inject
    private EvaluationResponseService evaluationResponseService;

    @Test
    void testBuildResponseWithError() {
        EvaluationResponse evaluationResponse = errorEvaluationResponse();
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.ERROR, LocalDateTime.now());
        ResponseDto<EvaluationResponseDto> evaluationResponseDto =
                evaluationResponseService.processResponse(evaluationResponse, evaluationRequestEntity);
        assertThat(evaluationResponseDto).isNotNull();
        assertThat(evaluationResponseDto.getPayload()).isNotNull();
        assertThat(evaluationResponseDto.getPayload().getRequestId()).isEqualTo(
                evaluationRequestEntity.getCorrelationId());
        assertThat(evaluationResponseDto.getRequestStatus()).isEqualTo(RequestStatus.ERROR);
    }

    @Test
    void testBuildSuccessResponse() {
        EvaluationResponse evaluationResponse = successEvaluationResponse();
        EvaluationRequestEntity evaluationRequestEntity =
                createEvaluationRequestEntity(RequestStageType.COMPLETED, LocalDateTime.now());
        String expectedModelUrl = String.format(MODEL_DOWNLOAD_URL_FORMAT, externalApiConfig.getDownloadBaseUrl(),
                evaluationRequestEntity.getCorrelationId());
        ResponseDto<EvaluationResponseDto> evaluationResponseDto =
                evaluationResponseService.processResponse(evaluationResponse, evaluationRequestEntity);
        assertThat(evaluationResponseDto).isNotNull();
        assertThat(evaluationResponseDto.getRequestStatus()).isEqualTo(RequestStatus.SUCCESS);
        assertThat(evaluationResponseDto.getPayload()).isNotNull();
        assertThat(evaluationResponseDto.getPayload().getRequestId()).isEqualTo(
                evaluationRequestEntity.getCorrelationId());
        assertThat(evaluationResponseDto.getPayload().getModelUrl()).isEqualTo(expectedModelUrl);
    }
}
