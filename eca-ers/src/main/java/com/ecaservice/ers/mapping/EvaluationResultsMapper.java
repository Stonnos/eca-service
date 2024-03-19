package com.ecaservice.ers.mapping;


import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.report.model.EvaluationResultsHistoryBean;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationResultsHistoryDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.ecaservice.ers.util.Utils.getEvaluationMethodDescription;

/**
 * Implements evaluation results mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {ClassificationCostsReportMapper.class, ConfusionMatrixMapper.class,
        StatisticsReportMapper.class, ClassifierReportMapper.class,
        ClassifierOptionsInfoMapper.class, InstancesMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EvaluationResultsMapper {

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Maps evaluation results request to evaluation results entity.
     *
     * @param evaluationResultsRequest - evaluation results report
     * @return evaluation results entity
     */
    @Mapping(source = "evaluationMethodReport.evaluationMethod", target = "evaluationMethod")
    @Mapping(source = "evaluationMethodReport.numFolds", target = "numFolds")
    @Mapping(source = "evaluationMethodReport.numTests", target = "numTests")
    @Mapping(source = "evaluationMethodReport.seed", target = "seed")
    @Mapping(source = "classifierReport", target = "classifierInfo")
    @Mapping(target = "instancesInfo", ignore = true)
    EvaluationResultsInfo map(EvaluationResultsRequest evaluationResultsRequest);

    /**
     * Maps evaluation results entity to response dto.
     *
     * @param evaluationResultsInfo - evaluation results info entity
     * @return evaluation results response
     */
    @Mapping(source = "evaluationMethod", target = "evaluationMethodReport.evaluationMethod")
    @Mapping(source = "numFolds", target = "evaluationMethodReport.numFolds")
    @Mapping(source = "numTests", target = "evaluationMethodReport.numTests")
    @Mapping(source = "seed", target = "evaluationMethodReport.seed")
    @Mapping(source = "classifierInfo", target = "classifierReport")
    @Mapping(source = "instancesInfo", target = "instances")
    GetEvaluationResultsResponse map(EvaluationResultsInfo evaluationResultsInfo);

    /**
     * Maps evaluation results entity to evaluation results history dto.
     *
     * @param evaluationResultsInfo - evaluation results info entity
     * @return evaluation results history dto
     */
    @Mapping(target = "evaluationMethod", ignore = true)
    @Mapping(target = "numFolds", ignore = true)
    @Mapping(target = "numTests", ignore = true)
    @Mapping(target = "seed", ignore = true)
    @Mapping(target = "classifierInfo", ignore = true)
    @Mapping(source = "statistics.numTestInstances", target = "numTestInstances")
    @Mapping(source = "statistics.numCorrect", target = "numCorrect")
    @Mapping(source = "statistics.numIncorrect", target = "numIncorrect")
    @Mapping(source = "statistics.pctCorrect", target = "pctCorrect")
    @Mapping(source = "statistics.pctIncorrect", target = "pctIncorrect")
    @Mapping(source = "statistics.meanAbsoluteError", target = "meanAbsoluteError")
    @Mapping(source = "statistics.rootMeanSquaredError", target = "rootMeanSquaredError")
    @Mapping(source = "statistics.maxAucValue", target = "maxAucValue")
    @Mapping(source = "statistics.varianceError", target = "varianceError")
    @Mapping(source = "statistics.confidenceIntervalLowerBound", target = "confidenceIntervalLowerBound")
    @Mapping(source = "statistics.confidenceIntervalUpperBound", target = "confidenceIntervalUpperBound")
    EvaluationResultsHistoryDto mapToEvaluationResultsHistory(EvaluationResultsInfo evaluationResultsInfo);

    /**
     * Maps evaluation results entity to evaluation results history report model.
     *
     * @param evaluationResultsInfo - evaluation results info entity
     * @return evaluation results history report model
     */
    @Mapping(target = "evaluationMethod", ignore = true)
    @Mapping(target = "classifierName", ignore = true)
    @Mapping(source = "saveDate", target = "saveDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "classifierInfo.options", target = "classifierOptions")
    @Mapping(source = "instancesInfo.relationName", target = "relationName")
    @Mapping(source = "statistics.numTestInstances", target = "numTestInstances")
    @Mapping(source = "statistics.numCorrect", target = "numCorrect")
    @Mapping(source = "statistics.numIncorrect", target = "numIncorrect")
    @Mapping(source = "statistics.pctCorrect", target = "pctCorrect")
    @Mapping(source = "statistics.pctIncorrect", target = "pctIncorrect")
    @Mapping(source = "statistics.meanAbsoluteError", target = "meanAbsoluteError")
    @Mapping(source = "statistics.rootMeanSquaredError", target = "rootMeanSquaredError")
    @Mapping(source = "statistics.maxAucValue", target = "maxAucValue")
    @Mapping(source = "statistics.varianceError", target = "varianceError")
    @Mapping(source = "statistics.confidenceIntervalLowerBound", target = "confidenceIntervalLowerBound")
    @Mapping(source = "statistics.confidenceIntervalUpperBound", target = "confidenceIntervalUpperBound")
    EvaluationResultsHistoryBean mapToEvaluationResultsHistoryBean(EvaluationResultsInfo evaluationResultsInfo);

    @AfterMapping
    default void mapEvaluationMethodOptions(EvaluationResultsInfo evaluationResultsInfo,
                                            @MappingTarget EvaluationResultsHistoryBean evaluationResultsHistoryBean) {
        String evaluationMethodDescription = getEvaluationMethodDescription(evaluationResultsInfo.getEvaluationMethod(),
                evaluationResultsInfo.getNumFolds(), evaluationResultsInfo.getNumTests());
        evaluationResultsHistoryBean.setEvaluationMethod(evaluationMethodDescription);
    }

    /**
     * Converts local date time to string in format yyy-MM-dd HH:mm:ss.
     *
     * @param localDateTime - local date time
     * @return date time string
     */
    @Named("formatLocalDateTime")
    default String formatLocalDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(DATE_TIME_FORMATTER::format).orElse(null);
    }

    /**
     * Maps evaluation method data
     *
     * @param evaluationResultsInfo       - evaluation results info entity
     * @param evaluationResultsHistoryDto - evaluation results history dto
     */
    @AfterMapping
    default void mapEvaluationMethod(EvaluationResultsInfo evaluationResultsInfo,
                                     @MappingTarget EvaluationResultsHistoryDto evaluationResultsHistoryDto) {
        evaluationResultsHistoryDto.setEvaluationMethod(new EnumDto(evaluationResultsInfo.getEvaluationMethod().name(),
                evaluationResultsInfo.getEvaluationMethod().getDescription()));
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationResultsInfo.getEvaluationMethod())) {
            evaluationResultsHistoryDto.setNumFolds(evaluationResultsInfo.getNumFolds());
            evaluationResultsHistoryDto.setNumTests(evaluationResultsInfo.getNumTests());
            evaluationResultsHistoryDto.setSeed(evaluationResultsInfo.getSeed());
        }
    }
}
