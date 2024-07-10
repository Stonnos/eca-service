package com.ecaservice.server.service.evaluation;

import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.web.dto.model.ConfusionMatrixCellDto;
import com.ecaservice.web.dto.model.ConfusionMatrixDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.ecaservice.server.TestHelperUtils.loadConfusionMatrixDto;
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
    private ConfusionMatrixDto expectedConfusionMatrixDto;

    @BeforeEach
    void init() {
        evaluationResultsResponse = loadEvaluationResultsResponse();
        expectedConfusionMatrixDto = loadConfusionMatrixDto();
    }

    @Test
    void testProcessConfusionMatrix() {
        var confusionMatrixDto = confusionMatrixService.proceedConfusionMatrix(evaluationResultsResponse);
        assertThat(confusionMatrixDto).isNotNull();
        assertThat(confusionMatrixDto.getConfusionMatrixCells()).isNotEmpty();
        assertThat(confusionMatrixDto.getConfusionMatrixCells().size()).isEqualTo(
                evaluationResultsResponse.getInstances().getNumClasses().intValue());
        assertThat(confusionMatrixDto.getConfusionMatrixCells()).hasSameClassAs(
                expectedConfusionMatrixDto.getConfusionMatrixCells());
        assertThat(confusionMatrixDto.getClassValues()).isEqualTo(expectedConfusionMatrixDto.getClassValues());
        for (int i = 0; i < confusionMatrixDto.getConfusionMatrixCells().size(); i++) {
            assertThat(confusionMatrixDto.getConfusionMatrixCells().get(i)).hasSameSizeAs(
                    expectedConfusionMatrixDto.getConfusionMatrixCells().get(i));
            for (int j = 0; j < confusionMatrixDto.getConfusionMatrixCells().size(); j++) {
                ConfusionMatrixCellDto expectedCell =
                        expectedConfusionMatrixDto.getConfusionMatrixCells().get(i).get(j);
                ConfusionMatrixCellDto actualCell =
                        confusionMatrixDto.getConfusionMatrixCells().get(i).get(j);
                assertThat(actualCell.getNumInstances()).isEqualTo(expectedCell.getNumInstances());
                assertThat(actualCell.getState()).isEqualTo(expectedCell.getState());
            }
        }
    }
}
