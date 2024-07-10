package com.ecaservice.server.service.evaluation;

import com.ecaservice.ers.dto.ConfusionMatrixReport;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.web.dto.model.ConfusionMatrixCellDto;
import com.ecaservice.web.dto.model.ConfusionMatrixCellState;
import com.ecaservice.web.dto.model.ConfusionMatrixDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Maps.newLinkedHashMap;

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
        var classIndexMap = createClassIndexes(evaluationResultsResponse);
        var confusionMatrixCells = createConfusionMatrixCells(classIndexMap.size());
        // Populate confusion matrix cells
        evaluationResultsResponse.getConfusionMatrix().forEach(confusionMatrixReport -> {
            int expectedClassIndex = classIndexMap.get(confusionMatrixReport.getPredictedClass());
            int actualClassIndex = classIndexMap.get(confusionMatrixReport.getActualClass());
            var confusionMatrixCellDto = confusionMatrixCells.get(actualClassIndex).get(expectedClassIndex);
            confusionMatrixCellDto.setNumInstances(confusionMatrixReport.getNumInstances().intValue());
        });
        // Populate confusion matrix cell states
        populateConfusionMatrixCellStates(confusionMatrixCells);
        confusionMatrixDto.setClassValues(classIndexMap.keySet().stream().toList());
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

    private Map<String, Integer> createClassIndexes(GetEvaluationResultsResponse evaluationResultsResponse) {
        Map<String, Integer> classIndexMap = newLinkedHashMap();
        int classIndex = 0;
        for (ConfusionMatrixReport confusionMatrixReport : evaluationResultsResponse.getConfusionMatrix()) {
            if (!classIndexMap.containsKey(confusionMatrixReport.getPredictedClass())) {
                classIndexMap.put(confusionMatrixReport.getPredictedClass(), classIndex);
                classIndex++;
            }
        }
        return classIndexMap;
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
