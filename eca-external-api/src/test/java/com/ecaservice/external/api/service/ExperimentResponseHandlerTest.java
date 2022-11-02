package com.ecaservice.external.api.service;

import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.external.api.AbstractJpaTest;
import com.ecaservice.external.api.entity.ExperimentRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapperImpl;
import com.ecaservice.external.api.repository.ExperimentRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.external.api.TestHelperUtils.createExperimentRequestEntity;
import static com.ecaservice.external.api.TestHelperUtils.errorExperimentResponse;
import static com.ecaservice.external.api.TestHelperUtils.successExperimentResponse;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link ExperimentResponseHandler} functionality.
 *
 * @author Roman Batygin
 */
@Import({ExperimentResponseHandler.class, EcaRequestMapperImpl.class, RequestStageHandler.class})
class ExperimentResponseHandlerTest extends AbstractJpaTest {

    @Inject
    private ExperimentRequestRepository experimentRequestRepository;

    @Inject
    private ExperimentResponseHandler experimentResponseHandler;

    @Override
    public void deleteAll() {
        experimentRequestRepository.deleteAll();
    }

    @Test
    void testSuccessExperimentResponseHandle() {
        var experimentRequestEntity =
                createExperimentRequestEntity(UUID.randomUUID().toString(), RequestStageType.REQUEST_SENT);
        experimentRequestRepository.save(experimentRequestEntity);
        var experimentResponse = successExperimentResponse();
        experimentResponseHandler.handleResponse(experimentRequestEntity, experimentResponse);
        var actual =
                internalTestResponseHandle(experimentRequestEntity, experimentResponse, RequestStageType.COMPLETED);
        assertThat(actual.getExperimentDownloadUrl()).isEqualTo(experimentResponse.getDownloadUrl());
    }

    @Test
    void testExperimentResponseHandleWithErrorTechnicalStatus() {
        var experimentRequestEntity =
                createExperimentRequestEntity(UUID.randomUUID().toString(), RequestStageType.REQUEST_SENT);
        experimentRequestRepository.save(experimentRequestEntity);
        var experimentResponse = errorExperimentResponse();
        internalTestResponseHandle(experimentRequestEntity, experimentResponse, RequestStageType.ERROR);
    }

    private ExperimentRequestEntity internalTestResponseHandle(ExperimentRequestEntity experimentRequestEntity,
                                                               ExperimentResponse experimentResponse,
                                                               RequestStageType expected) {
        experimentResponseHandler.handleResponse(experimentRequestEntity, experimentResponse);
        var actual =
                experimentRequestRepository.findById(experimentRequestEntity.getId()).orElse(null);
        assertThat(actual).isNotNull();
        assertThat(actual.getEndDate()).isNotNull();
        assertThat(actual.getRequestStage()).isEqualTo(expected);
        return actual;
    }
}
