package com.ecaservice.server.service.evaluation;

import com.ecaservice.ers.dto.ClassificationCostsReport;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.web.dto.model.ConfusionMatrixCellDto;
import com.ecaservice.web.dto.model.ConfusionMatrixCellState;
import com.ecaservice.web.dto.model.ConfusionMatrixDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Confusion matrix service.
 *
 * @author Roman Batygin
 */
@Service
public class ConfusionMatrixService {

    /**
     * Proceed confusion matrix from evaluation results response.
     *
     * @param evaluationResultsResponse - evaluation result response
     * @return confusion matrix dto
     */
    public ConfusionMatrixDto proceedConfusionMatrix(GetEvaluationResultsResponse evaluationResultsResponse) {
        ConfusionMatrixDto confusionMatrixDto = new ConfusionMatrixDto();
        List<String> classValues  = evaluationResultsResponse.getClassificationCosts()
                .stream()
                .map(ClassificationCostsReport::getClassValue)
                .toList();
        var confusionMatrixCells = createConfusionMatrixCells(classValues.size());
        // Populate confusion matrix cells
        evaluationResultsResponse.getConfusionMatrix().forEach(confusionMatrixReport -> {
            int predictedClassIndex = confusionMatrixReport.getPredictedClassIndex();
            int actualClassIndex = confusionMatrixReport.getActualClassIndex();
            var confusionMatrixCellDto = confusionMatrixCells.get(actualClassIndex).get(predictedClassIndex);
            confusionMatrixCellDto.setNumInstances(confusionMatrixReport.getNumInstances().intValue());
        });
        // Populate confusion matrix cell states
        populateConfusionMatrixCellStates(confusionMatrixCells);
        confusionMatrixDto.setClassValues(classValues);
        confusionMatrixDto.setConfusionMatrixCells(confusionMatrixCells);
        return confusionMatrixDto;
    }

    private void populateConfusionMatrixCellStates(List<List<ConfusionMatrixCellDto>> confusionMatrixCells) {
        for (int i = 0; i < confusionMatrixCells.size(); i++) {
            boolean hasPredictedAndActualMisMatch = false;
            for (int j = 0; j < confusionMatrixCells.size(); j++) {
                if (i != j && confusionMatrixCells.get(i).get(j).getNumInstances() > 0) {
                    hasPredictedAndActualMisMatch = true;
                    confusionMatrixCells.get(i).get(j).setState(ConfusionMatrixCellState.YELLOW);
                }
            }
            // Sets cell state for diagonal cell
            var state = hasPredictedAndActualMisMatch ? ConfusionMatrixCellState.YELLOW :
                    ConfusionMatrixCellState.GREEN;
            confusionMatrixCells.get(i).get(i).setState(state);
        }
    }

    private List<List<ConfusionMatrixCellDto>> createConfusionMatrixCells(int numClasses) {
        return Stream.generate(() -> createConfusionMatrixRow(numClasses))
                .limit(numClasses)
                .collect(Collectors.toList());
    }

    private List<ConfusionMatrixCellDto> createConfusionMatrixRow(int numClasses) {
        return Stream.generate(() -> {
                    ConfusionMatrixCellDto confusionMatrixCellDto = new ConfusionMatrixCellDto();
                    confusionMatrixCellDto.setState(ConfusionMatrixCellState.WHITE);
                    return confusionMatrixCellDto;
                })
                .limit(numClasses)
                .collect(Collectors.toList());
    }
}
