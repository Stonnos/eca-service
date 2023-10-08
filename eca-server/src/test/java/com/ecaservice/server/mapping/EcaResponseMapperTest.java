package com.ecaservice.server.mapping;

import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createEvaluationResultsModel;
import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests that checks EcaResponseMapper functionality {@see EcaResponseMapper}.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(EcaResponseMapperImpl.class)
class EcaResponseMapperTest {

    private final Map<RequestStatus, TechnicalStatus> requestStatusTechnicalStatusMap = Map.of(
            RequestStatus.NEW, TechnicalStatus.IN_PROGRESS,
            RequestStatus.IN_PROGRESS, TechnicalStatus.IN_PROGRESS,
            RequestStatus.FINISHED, TechnicalStatus.SUCCESS,
            RequestStatus.TIMEOUT, TechnicalStatus.TIMEOUT,
            RequestStatus.ERROR, TechnicalStatus.ERROR
    );

    @Inject
    private EcaResponseMapper ecaResponseMapper;

    @Test
    void testMapExperimentToEcaResponse() {
        requestStatusTechnicalStatusMap.forEach(this::internalTestMapExperimentToEcaResponse);
    }

    @Test
    void testMapEvaluationResultsDataModel() {
        var evaluationResultsModel = createEvaluationResultsModel(UUID.randomUUID().toString());
        var evaluationResponse = ecaResponseMapper.map(evaluationResultsModel);
        assertThat(evaluationResponse).isNotNull();
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
        assertThat(evaluationResponse.getModelUrl()).isEqualTo(evaluationResultsModel.getModelUrl());
        assertThat(evaluationResponse.getRequestId()).isEqualTo(evaluationResultsModel.getRequestId());
        assertThat(evaluationResponse.getPctCorrect()).isEqualTo(
                evaluationResultsModel.getPctCorrect());
        assertThat(evaluationResponse.getPctIncorrect()).isEqualTo(
                evaluationResultsModel.getPctIncorrect());
        assertThat(evaluationResponse.getMeanAbsoluteError()).isEqualTo(
                evaluationResultsModel.getMeanAbsoluteError());
        assertThat(evaluationResponse.getNumTestInstances()).isEqualTo(
                evaluationResultsModel.getNumTestInstances());
        assertThat(evaluationResponse.getNumCorrect()).isEqualTo(
                evaluationResultsModel.getNumCorrect());
        assertThat(evaluationResponse.getNumIncorrect()).isEqualTo(
                evaluationResultsModel.getNumIncorrect());
    }

    private void internalTestMapExperimentToEcaResponse(RequestStatus requestStatus, TechnicalStatus expectedStatus) {
        Experiment experiment = createExperiment(UUID.randomUUID().toString(), requestStatus);
        ExperimentResponse experimentResponse = ecaResponseMapper.map(experiment);
        assertThat(experimentResponse).isNotNull();
        assertThat(experimentResponse.getStatus()).isEqualTo(expectedStatus);
        assertThat(experimentResponse.getRequestId()).isEqualTo(experiment.getRequestId());
    }
}
