package com.ecaservice.server.service.evaluation;

import com.ecaservice.ers.dto.ClassificationCostsReport;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.model.evaluation.ConfusionMatrixCellData;
import com.ecaservice.server.model.evaluation.ConfusionMatrixData;
import com.ecaservice.web.dto.model.ConfusionMatrixCellState;
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
     * @return confusion matrix data
     */
    public ConfusionMatrixData proceedConfusionMatrix(GetEvaluationResultsResponse evaluationResultsResponse) {
        ConfusionMatrixData confusionMatrixData = new ConfusionMatrixData();
        List<String> classValues  = evaluationResultsResponse.getClassificationCosts()
                .stream()
                .map(ClassificationCostsReport::getClassValue)
                .toList();
        var confusionMatrixCells = createConfusionMatrixCells(classValues.size());
        // Populate confusion matrix cells
        evaluationResultsResponse.getConfusionMatrix().forEach(confusionMatrixReport -> {
            int predictedClassIndex = confusionMatrixReport.getPredictedClassIndex();
            int actualClassIndex = confusionMatrixReport.getActualClassIndex();
            var confusionMatrixCellData = confusionMatrixCells.get(actualClassIndex).get(predictedClassIndex);
            confusionMatrixCellData.setNumInstances(confusionMatrixReport.getNumInstances().intValue());
        });
        // Populate confusion matrix cell states
        populateConfusionMatrixCellStates(confusionMatrixCells);
        confusionMatrixData.setClassValues(classValues);
        confusionMatrixData.setConfusionMatrixCells(confusionMatrixCells);
        return confusionMatrixData;
    }

    private void populateConfusionMatrixCellStates(List<List<ConfusionMatrixCellData>> confusionMatrixCells) {
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

    private List<List<ConfusionMatrixCellData>> createConfusionMatrixCells(int numClasses) {
        return Stream.generate(() -> createConfusionMatrixRow(numClasses))
                .limit(numClasses)
                .collect(Collectors.toList());
    }

    private List<ConfusionMatrixCellData> createConfusionMatrixRow(int numClasses) {
        return Stream.generate(() -> {
                    ConfusionMatrixCellData confusionMatrixCellData = new ConfusionMatrixCellData();
                    confusionMatrixCellData.setState(ConfusionMatrixCellState.WHITE);
                    return confusionMatrixCellData;
                })
                .limit(numClasses)
                .collect(Collectors.toList());
    }
}
