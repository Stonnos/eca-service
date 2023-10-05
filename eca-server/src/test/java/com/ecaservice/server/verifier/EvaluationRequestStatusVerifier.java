package com.ecaservice.server.verifier;

import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import com.ecaservice.server.model.entity.RequestStatus;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Evaluation request status verifier.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class EvaluationRequestStatusVerifier implements TestStepVerifier<AbstractEvaluationEntity> {

    private final RequestStatus expectedRequestStatus;

    @Override
    public void verifyStep(AbstractEvaluationEntity evaluationEntity) {
        assertThat(evaluationEntity).isNotNull();
        assertThat(evaluationEntity.getRequestStatus()).isEqualTo(expectedRequestStatus);
    }
}
