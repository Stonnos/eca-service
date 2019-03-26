package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.GetEvaluationResultsSimpleResponse;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.Optional;

/**
 * Implements evaluation request to evaluation log mapping.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {InstancesInfoMapper.class, EvaluationLogInputOptionsMapper.class, StatisticsReportMapper.class})
public abstract class EvaluationLogDetailsMapper {

    /**
     * Maps evaluation log entity and evaluation results response to evaluation log details.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log details dto
     */
    @Mappings({
            @Mapping(source = "evaluationMethod.description", target = "evaluationMethod"),
            @Mapping(source = "evaluationStatus.description", target = "evaluationStatus"),
            @Mapping(source = "classifierInputOptions", target = "inputOptions")
    })
    public abstract EvaluationLogDetailsDto map(EvaluationLog evaluationLog);

    /**
     * Updates evaluation log details.
     *
     * @param evaluationResultsSimpleResponse - evaluation results simple response
     * @param evaluationLogDetailsDto         - evaluation log details
     */
    @Mappings({
            @Mapping(source = "statistics", target = "evaluationResultsDto"),
            @Mapping(target = "requestId", ignore = true)
    })
    public abstract void update(GetEvaluationResultsSimpleResponse evaluationResultsSimpleResponse,
                                @MappingTarget EvaluationLogDetailsDto evaluationLogDetailsDto);

    @AfterMapping
    protected void mapEvaluationMethodOptions(EvaluationLog evaluationLog,
                                              @MappingTarget EvaluationLogDto evaluationLogDto) {
        evaluationLogDto.setNumFolds(
                Optional.ofNullable(evaluationLog.getEvaluationOptionsMap().get(EvaluationOption.NUM_FOLDS)).map(
                        Integer::valueOf).orElse(null));
        evaluationLogDto.setNumTests(
                Optional.ofNullable(evaluationLog.getEvaluationOptionsMap().get(EvaluationOption.NUM_TESTS)).map(
                        Integer::valueOf).orElse(null));
        evaluationLogDto.setSeed(
                Optional.ofNullable(evaluationLog.getEvaluationOptionsMap().get(EvaluationOption.SEED)).map(
                        Integer::valueOf).orElse(null));
    }
}
