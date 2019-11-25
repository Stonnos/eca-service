package com.ecaservice.mapping;

import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.InstancesInfo;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Optional;

/**
 * Implements evaluation request to evaluation log mapping.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {InstancesInfoMapper.class, ClassifierInfoMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class EvaluationLogMapper {

    /**
     * Maps evaluation request to evaluation log.
     *
     * @param evaluationRequest evaluation request
     * @return evaluation log entity
     */
    @Mapping(source = "classifier", target = "classifierInfo")
    public abstract EvaluationLog map(EvaluationRequest evaluationRequest);

    /**
     * Maps evaluation log entity to its dto model.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log dto
     */
    @Mapping(target = "evaluationMethod", ignore = true)
    @Mapping(target = "evaluationStatus", ignore = true)
    public abstract EvaluationLogDto map(EvaluationLog evaluationLog);

    /**
     * Maps evaluation log entity to evaluation log details.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log details dto
     */
    @Mapping(target = "evaluationMethod", ignore = true)
    @Mapping(target = "evaluationStatus", ignore = true)
    public abstract EvaluationLogDetailsDto mapDetails(EvaluationLog evaluationLog);

    @AfterMapping
    protected void mapData(EvaluationRequest evaluationRequest, @MappingTarget EvaluationLog evaluationLog) {
        if (evaluationRequest.getData() != null) {
            InstancesInfo instancesInfo = new InstancesInfo();
            instancesInfo.setRelationName(evaluationRequest.getData().relationName());
            instancesInfo.setNumInstances(evaluationRequest.getData().numInstances());
            instancesInfo.setNumAttributes(evaluationRequest.getData().numAttributes());
            instancesInfo.setNumClasses(evaluationRequest.getData().numClasses());
            instancesInfo.setClassName(evaluationRequest.getData().classAttribute().name());
            evaluationLog.setInstancesInfo(instancesInfo);
        }
    }

    @AfterMapping
    protected void mapEvaluationMethodOptions(EvaluationLog evaluationLog,
                                              @MappingTarget EvaluationLogDto evaluationLogDto) {
        evaluationLogDto.setEvaluationMethod(new EnumDto(evaluationLog.getEvaluationMethod().name(),
                evaluationLog.getEvaluationMethod().getDescription()));
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

    @AfterMapping
    protected void mapEvaluationStatus(EvaluationLog evaluationLog,
                                       @MappingTarget EvaluationLogDto evaluationLogDto) {
        evaluationLogDto.setEvaluationStatus(new EnumDto(evaluationLog.getEvaluationStatus().name(),
                evaluationLog.getEvaluationStatus().getDescription()));
    }
}
