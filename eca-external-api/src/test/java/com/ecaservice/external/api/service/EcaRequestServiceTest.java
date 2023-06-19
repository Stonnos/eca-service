package com.ecaservice.external.api.service;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapperImpl;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.repository.ExperimentRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestDto;
import static com.ecaservice.external.api.TestHelperUtils.createExperimentRequestDto;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EcaRequestService} class.
 *
 * @author Roman Batygin
 */
@Import({EcaRequestService.class, EcaRequestMapperImpl.class, ExternalApiConfig.class})
class EcaRequestServiceTest extends AbstractJpaTest {

    @Inject
    private EvaluationRequestRepository evaluationRequestRepository;
    @Inject
    private ExperimentRequestRepository experimentRequestRepository;

    @Inject
    private EcaRequestService ecaRequestService;

    @Test
    void testSaveEvaluationRequestEntity() {
        var evaluationRequestDto = createEvaluationRequestDto();
        var ecaRequestEntity = ecaRequestService.createAndSaveEvaluationRequestEntity(evaluationRequestDto);
        assertThat(ecaRequestEntity).isNotNull();
        var actual = evaluationRequestRepository.findById(ecaRequestEntity.getId()).orElse(null);
        assertCreatedRequestEntityBaseFields(actual);
        assertThat(actual.isUseOptimalClassifierOptions()).isFalse();
    }

    @Test
    void testSaveEvaluationRequestEntityWithUseOptimalClassifierOptionsFlag() {
        var ecaRequestEntity = ecaRequestService.createAndSaveEvaluationOptimizerRequestEntity();
        assertThat(ecaRequestEntity).isNotNull();
        var actual = evaluationRequestRepository.findById(ecaRequestEntity.getId()).orElse(null);
        assertCreatedRequestEntityBaseFields(actual);
        assertThat(actual.isUseOptimalClassifierOptions()).isTrue();
    }

    @Test
    void testSaveExperimentRequestEntity() {
        var experimentRequestDto = createExperimentRequestDto();
        var ecaRequestEntity = ecaRequestService.createAndSaveExperimentRequestEntity(experimentRequestDto);
        assertThat(ecaRequestEntity).isNotNull();
        var actual = experimentRequestRepository.findById(ecaRequestEntity.getId()).orElse(null);
        assertCreatedRequestEntityBaseFields(actual);
        assertThat(actual.getExperimentType()).isNotNull();
        assertThat(actual.getEvaluationMethod()).isEqualTo(experimentRequestDto.getEvaluationMethod());
    }

    private void assertCreatedRequestEntityBaseFields(EcaRequestEntity ecaRequestEntity) {
        assertThat(ecaRequestEntity).isNotNull();
        assertThat(ecaRequestEntity).isNotNull();
        assertThat(ecaRequestEntity.getRequestStage()).isEqualTo(RequestStageType.READY);
        assertThat(ecaRequestEntity.getCreationDate()).isNotNull();
        assertThat(ecaRequestEntity.getCorrelationId()).isNotNull();
    }
}
