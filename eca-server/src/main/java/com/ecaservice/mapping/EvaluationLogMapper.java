package com.ecaservice.mapping;

import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.InstancesInfo;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.web.dto.EvaluationLogDto;
import com.ecaservice.web.dto.InputOptionDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implements evaluation request to evaluation log mapping.
 *
 * @author Roman Batygin
 */
@Mapper(uses = InstancesInfoMapper.class)
public abstract class EvaluationLogMapper {

    /**
     * Maps evaluation request to evaluation log.
     *
     * @param evaluationRequest evaluation request
     * @return evaluation log entity
     */
    public abstract EvaluationLog map(EvaluationRequest evaluationRequest);

    /**
     * Maps evaluation log entity to its dto model.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log dto
     */
    public abstract EvaluationLogDto map(EvaluationLog evaluationLog);

    /**
     * Maps evaluation log entities to dto models.
     *
     * @param evaluationLog - evaluation log entities list
     * @return evaluation logs dto list
     */
    public abstract List<EvaluationLogDto> map(List<EvaluationLog> evaluationLog);

    @AfterMapping
    protected void mapClassifier(EvaluationRequest evaluationRequest, @MappingTarget EvaluationLog evaluationLog) {
        if (evaluationRequest.getClassifier() != null) {
            evaluationLog.setClassifierName(evaluationRequest.getClassifier().getClass().getSimpleName());
            String[] options = evaluationRequest.getClassifier().getOptions();
            Map<String, String> optionsMap = new HashMap<>();
            for (int i = 0; i < options.length; i += 2) {
                optionsMap.put(options[i], options[i + 1]);
            }
            evaluationLog.setInputOptionsMap(optionsMap);
        }
    }

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
    protected void mapInputOptions(EvaluationLog evaluationLog, @MappingTarget EvaluationLogDto evaluationLogDto) {
        if (!CollectionUtils.isEmpty(evaluationLog.getInputOptionsMap())) {
            evaluationLogDto.setInputOptions(evaluationLog.getInputOptionsMap().entrySet().stream().map(
                    entry -> new InputOptionDto(entry.getKey(), entry.getValue())).collect(Collectors.toList()));
        }
    }
}
