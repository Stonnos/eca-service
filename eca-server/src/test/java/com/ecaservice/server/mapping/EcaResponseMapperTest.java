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

import static com.ecaservice.server.TestHelperUtils.createEvaluationResultsDataModel;
import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static com.ecaservice.server.TestHelperUtils.getEvaluationResults;
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
        var evaluationResultsDataModel = createEvaluationResultsDataModel(UUID.randomUUID().toString());
        var evaluationResults = getEvaluationResults();
        evaluationResultsDataModel.setEvaluationResults(evaluationResults);
        var evaluationResponse = ecaResponseMapper.map(evaluationResultsDataModel);
        assertThat(evaluationResponse).isNotNull();
        assertThat(evaluationResponse.getStatus()).isEqualTo(TechnicalStatus.SUCCESS);
        assertThat(evaluationResponse.getModelUrl()).isEqualTo(evaluationResultsDataModel.getModelUrl());
        assertThat(evaluationResponse.getRequestId()).isEqualTo(evaluationResultsDataModel.getRequestId());
        assertThat(evaluationResponse.getPctCorrect().doubleValue()).isEqualTo(
                evaluationResults.getEvaluation().pctCorrect());
        assertThat(evaluationResponse.getPctIncorrect().doubleValue()).isEqualTo(
                evaluationResults.getEvaluation().pctIncorrect());
        assertThat(evaluationResponse.getMeanAbsoluteError().doubleValue()).isEqualTo(
                evaluationResults.getEvaluation().meanAbsoluteError());
        assertThat(evaluationResponse.getNumTestInstances().doubleValue()).isEqualTo(
                evaluationResults.getEvaluation().numInstances());
        assertThat(evaluationResponse.getNumCorrect().doubleValue()).isEqualTo(
                evaluationResults.getEvaluation().correct());
        assertThat(evaluationResponse.getNumIncorrect().doubleValue()).isEqualTo(
                evaluationResults.getEvaluation().incorrect());
    }

    private void internalTestMapExperimentToEcaResponse(RequestStatus requestStatus, TechnicalStatus expectedStatus) {
        Experiment experiment = createExperiment(UUID.randomUUID().toString(), requestStatus);
        ExperimentResponse experimentResponse = ecaResponseMapper.map(experiment);
        assertThat(experimentResponse).isNotNull();
        assertThat(experimentResponse.getStatus()).isEqualTo(expectedStatus);
        assertThat(experimentResponse.getRequestId()).isEqualTo(experiment.getRequestId());
    }
}
