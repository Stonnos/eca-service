package com.ecaservice.mapping;

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
 * Implements evaluation log to evaluation log details mapping.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {InstancesInfoMapper.class, ClassifierInfoMapper.class})
public abstract class EvaluationLogDetailsMapper {

    /**
     * Maps evaluation log entity to evaluation log details.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log details dto
     */
    @Mappings({
            @Mapping(target = "evaluationMethod", ignore = true),
            @Mapping(target = "evaluationStatus", ignore = true)
    })
    public abstract EvaluationLogDetailsDto map(EvaluationLog evaluationLog);

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
