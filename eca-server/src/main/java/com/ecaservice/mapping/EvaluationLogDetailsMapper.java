package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
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
@Mapper(uses = {InstancesInfoMapper.class, EvaluationLogInputOptionsMapper.class,
        StatisticsReportMapper.class, ClassificationCostsMapper.class})
public abstract class EvaluationLogDetailsMapper {

    /**
     * Maps evaluation log entity and evaluation results response to evaluation log details.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log details dto
     */
    @Mappings({
            //@Mapping(source = "classifierInputOptions", target = "inputOptions"),
            @Mapping(target = "evaluationMethod", ignore = true),
            @Mapping(target = "evaluationStatus", ignore = true)
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
    public abstract void update(GetEvaluationResultsResponse evaluationResultsSimpleResponse,
                                @MappingTarget EvaluationLogDetailsDto evaluationLogDetailsDto);

    @AfterMapping
    protected void mapEvaluationMethodOptions(EvaluationLog evaluationLog,
                                              @MappingTarget EvaluationLogDetailsDto evaluationLogDetailsDto) {
        evaluationLogDetailsDto.setEvaluationMethod(new EnumDto(evaluationLog.getEvaluationMethod().name(),
                evaluationLog.getEvaluationMethod().getDescription()));
        evaluationLogDetailsDto.setNumFolds(
                Optional.ofNullable(evaluationLog.getEvaluationOptionsMap().get(EvaluationOption.NUM_FOLDS)).map(
                        Integer::valueOf).orElse(null));
        evaluationLogDetailsDto.setNumTests(
                Optional.ofNullable(evaluationLog.getEvaluationOptionsMap().get(EvaluationOption.NUM_TESTS)).map(
                        Integer::valueOf).orElse(null));
        evaluationLogDetailsDto.setSeed(
                Optional.ofNullable(evaluationLog.getEvaluationOptionsMap().get(EvaluationOption.SEED)).map(
                        Integer::valueOf).orElse(null));
    }

    @AfterMapping
    protected void mapEvaluationStatus(EvaluationLog evaluationLog,
                                       @MappingTarget EvaluationLogDetailsDto evaluationLogDetailsDto) {
        evaluationLogDetailsDto.setEvaluationStatus(new EnumDto(evaluationLog.getEvaluationStatus().name(),
                evaluationLog.getEvaluationStatus().getDescription()));
    }
}
