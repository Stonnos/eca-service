package com.ecaservice.external.api.service;

import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapperImpl;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestDto;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EcaRequestService} class.
 *
 * @author Roman Batygin
 */
@Import({EcaRequestService.class, EcaRequestMapperImpl.class})
class EcaRequestServiceTest extends AbstractJpaTest {

    @Inject
    private EvaluationRequestRepository evaluationRequestRepository;

    @Inject
    private EcaRequestService ecaRequestService;

    @Test
    void testSaveRequestEntity() {
        EvaluationRequestDto evaluationRequestDto = createEvaluationRequestDto();
        EcaRequestEntity ecaRequestEntity = ecaRequestService.createAndSaveRequestEntity(evaluationRequestDto);
        assertThat(ecaRequestEntity).isNotNull();
        EvaluationRequestEntity actual = evaluationRequestRepository.findById(ecaRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(RequestStageType.READY);
        assertThat(actual.getCreationDate()).isNotNull();
        assertThat(actual.getCorrelationId()).isNotNull();
    }
}
