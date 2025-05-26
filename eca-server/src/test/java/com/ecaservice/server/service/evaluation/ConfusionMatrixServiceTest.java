package com.ecaservice.server.service.evaluation;

import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.model.evaluation.ConfusionMatrixCellData;
import com.ecaservice.server.model.evaluation.ConfusionMatrixData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.ecaservice.server.TestHelperUtils.loadConfusionMatrixData;
import static com.ecaservice.server.TestHelperUtils.loadEvaluationResultsResponse;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for {@link ConfusionMatrixService} class.
 *
 * @author Roman Batygin
 */
class ConfusionMatrixServiceTest {

    private final ConfusionMatrixService confusionMatrixService = new ConfusionMatrixService();

    private GetEvaluationResultsResponse evaluationResultsResponse;
    private ConfusionMatrixData expectedConfusionMatrixData;

    @BeforeEach
    void init() {
        evaluationResultsResponse = loadEvaluationResultsResponse();
        expectedConfusionMatrixData = loadConfusionMatrixData();
    }

    @Test
    void testProcessConfusionMatrix() {
        var confusionMatrixData = confusionMatrixService.proceedConfusionMatrix(evaluationResultsResponse);
        assertThat(confusionMatrixData).isNotNull();
        assertThat(confusionMatrixData.getConfusionMatrixCells()).isNotEmpty();
        assertThat(confusionMatrixData.getConfusionMatrixCells().size()).isEqualTo(
                evaluationResultsResponse.getInstances().getNumClasses().intValue());
        assertThat(confusionMatrixData.getConfusionMatrixCells()).hasSameClassAs(
                expectedConfusionMatrixData.getConfusionMatrixCells());
        assertThat(confusionMatrixData.getClassValues()).isEqualTo(expectedConfusionMatrixData.getClassValues());
        for (int i = 0; i < confusionMatrixData.getConfusionMatrixCells().size(); i++) {
            assertThat(confusionMatrixData.getConfusionMatrixCells().get(i)).hasSameSizeAs(
                    expectedConfusionMatrixData.getConfusionMatrixCells().get(i));
            for (int j = 0; j < confusionMatrixData.getConfusionMatrixCells().size(); j++) {
                ConfusionMatrixCellData expectedCell =
                        expectedConfusionMatrixData.getConfusionMatrixCells().get(i).get(j);
                ConfusionMatrixCellData actualCell =
                        confusionMatrixData.getConfusionMatrixCells().get(i).get(j);
                assertThat(actualCell.getNumInstances()).isEqualTo(expectedCell.getNumInstances());
                assertThat(actualCell.getState()).isEqualTo(expectedCell.getState());
            }
        }
    }
}
