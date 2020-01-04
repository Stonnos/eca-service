package com.ecaservice.mapping;

import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.InstancesInfo;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.report.model.EvaluationLogBean;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Implements evaluation request to evaluation log mapping.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {InstancesInfoMapper.class, ClassifierInfoMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class EvaluationLogMapper {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    /**
     * Maps evaluation log entity to evaluation log report bean.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log bean
     */
    @Mapping(source = "evaluationMethod.description", target = "evaluationMethod")
    @Mapping(source = "evaluationStatus.description", target = "evaluationStatus")
    @Mapping(source = "creationDate", target = "creationDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "classifierInfo.classifierName", target = "classifierName")
    @Mapping(source = "instancesInfo.relationName", target = "relationName")
    public abstract EvaluationLogBean mapToBean(EvaluationLog evaluationLog);

    /**
     * Maps evaluation logs entities to evaluation log reports beans.
     *
     * @param evaluationLogs - evaluation logs entities list
     * @return evaluation logs beans list
     */
    public abstract List<EvaluationLogBean> mapToBeans(List<EvaluationLog> evaluationLogs);

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

    @Named("formatLocalDateTime")
    protected String formatLocalDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(dateTimeFormatter::format).orElse(null);
    }
}
