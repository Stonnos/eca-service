package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.ClassificationCostsReport;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import com.ecaservice.server.report.model.ClassificationCostsReportBean;
import com.ecaservice.server.report.model.EvaluationResultsReportBean;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

import static com.ecaservice.server.util.Utils.getEvaluationMethodDescription;

/**
 * Evaluation results report data mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface EvaluationResultsReportDataMapper {

    /**
     * Maps input data to evaluation results report.
     *
     * @param evaluationEntity          - evaluation entity
     * @param evaluationResultsResponse - evaluation results response
     * @return evaluation results report data
     */
    @Mapping(source = "evaluationEntity.requestId", target = "requestId")
    @Mapping(source = "evaluationEntity.instancesInfo.relationName", target = "relationName")
    @Mapping(source = "evaluationEntity.instancesInfo.numInstances", target = "numInstances")
    @Mapping(source = "evaluationEntity.instancesInfo.numAttributes", target = "numAttributes")
    @Mapping(source = "evaluationEntity.instancesInfo.numClasses", target = "numClasses")
    @Mapping(source = "evaluationEntity.instancesInfo.className", target = "className")
    @Mapping(source = "evaluationResultsResponse.statistics.numTestInstances", target = "numTestInstances")
    @Mapping(source = "evaluationResultsResponse.statistics.numCorrect", target = "numCorrect")
    @Mapping(source = "evaluationResultsResponse.statistics.numIncorrect", target = "numIncorrect")
    @Mapping(source = "evaluationResultsResponse.statistics.pctCorrect", target = "pctCorrect")
    @Mapping(source = "evaluationResultsResponse.statistics.pctIncorrect", target = "pctIncorrect")
    @Mapping(source = "evaluationResultsResponse.statistics.meanAbsoluteError", target = "meanAbsoluteError")
    @Mapping(source = "evaluationResultsResponse.statistics.rootMeanSquaredError", target = "rootMeanSquaredError")
    @Mapping(source = "evaluationResultsResponse.statistics.varianceError", target = "varianceError")
    @Mapping(source = "evaluationResultsResponse.statistics.confidenceIntervalLowerBound",
            target = "confidenceIntervalLowerBound")
    @Mapping(source = "evaluationResultsResponse.statistics.confidenceIntervalUpperBound",
            target = "confidenceIntervalUpperBound")
    @Mapping(source = "evaluationResultsResponse.classificationCosts", target = "classificationCosts")
    @Mapping(target = "evaluationMethod", ignore = true)
    @Mapping(target = "classValues", ignore = true)
    @Mapping(target = "classifierInfo", ignore = true)
    @Mapping(target = "confusionMatrixCells", ignore = true)
    EvaluationResultsReportBean map(AbstractEvaluationEntity evaluationEntity,
                                    GetEvaluationResultsResponse evaluationResultsResponse);

    /**
     * Maps classification costs report data.
     *
     * @param classificationCostsReport - classification costs report
     * @return classification costs report bean
     */
    @Mapping(source = "rocCurve.aucValue", target = "aucValue")
    ClassificationCostsReportBean map(ClassificationCostsReport classificationCostsReport);

    /**
     * Maps classification costs report data list.
     *
     * @param classificationCosts - classification costs list
     * @return classification costs report data list
     */
    List<ClassificationCostsReportBean> map(List<ClassificationCostsReport> classificationCosts);

    @AfterMapping
    default void mapClassValues(GetEvaluationResultsResponse evaluationResultsResponse,
                                @MappingTarget EvaluationResultsReportBean evaluationResultsReportBean) {
        List<String> classValues = evaluationResultsResponse.getClassificationCosts()
                .stream()
                .map(ClassificationCostsReport::getClassValue)
                .toList();
        evaluationResultsReportBean.setClassValues(classValues);
    }

    @AfterMapping
    default void mapEvaluationMethodOptions(AbstractEvaluationEntity evaluationEntity,
                                            @MappingTarget EvaluationResultsReportBean evaluationResultsReportBean) {
        String evaluationMethodDescription =
                getEvaluationMethodDescription(evaluationEntity.getEvaluationMethod(), evaluationEntity.getNumFolds(),
                        evaluationEntity.getNumTests());
        evaluationResultsReportBean.setEvaluationMethod(evaluationMethodDescription);
    }
}
