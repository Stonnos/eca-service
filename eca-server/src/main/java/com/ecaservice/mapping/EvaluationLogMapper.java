package com.ecaservice.mapping;

import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.InstancesInfo;
import com.ecaservice.report.model.EvaluationLogBean;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import eca.core.evaluation.EvaluationMethod;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Optional;

import static com.ecaservice.util.Utils.getEvaluationMethodDescription;

/**
 * Implements evaluation request to evaluation log mapping.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {InstancesInfoMapper.class, ClassifierInfoMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class EvaluationLogMapper extends AbstractEvaluationMapper {

    /**
     * Maps evaluation request to evaluation log.
     *
     * @param evaluationRequest     - evaluation request
     * @param crossValidationConfig - cross validation config
     * @return evaluation log entity
     */
    @Mapping(source = "evaluationRequest.classifier", target = "classifierInfo")
    @Mapping(target = "numFolds", ignore = true)
    @Mapping(target = "numTests", ignore = true)
    @Mapping(target = "seed", ignore = true)
    public abstract EvaluationLog map(EvaluationRequest evaluationRequest, CrossValidationConfig crossValidationConfig);

    /**
     * Maps evaluation log entity to its dto model.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log dto
     */
    @Mapping(source = "evaluationLog", target = "evaluationTotalTime", qualifiedByName = "calculateEvaluationTotalTime")
    @Mapping(target = "evaluationMethod", ignore = true)
    @Mapping(target = "requestStatus", ignore = true)
    @Mapping(target = "numFolds", ignore = true)
    @Mapping(target = "numTests", ignore = true)
    @Mapping(target = "seed", ignore = true)
    public abstract EvaluationLogDto map(EvaluationLog evaluationLog);

    /**
     * Maps evaluation log entity to evaluation log details.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log details dto
     */
    @Mapping(source = "evaluationLog", target = "evaluationTotalTime", qualifiedByName = "calculateEvaluationTotalTime")
    @Mapping(target = "evaluationMethod", ignore = true)
    @Mapping(target = "requestStatus", ignore = true)
    @Mapping(target = "numFolds", ignore = true)
    @Mapping(target = "numTests", ignore = true)
    @Mapping(target = "seed", ignore = true)
    public abstract EvaluationLogDetailsDto mapDetails(EvaluationLog evaluationLog);

    /**
     * Maps evaluation log entity to evaluation log report bean.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log bean
     */
    @Mapping(target = "evaluationMethod", ignore = true)
    @Mapping(source = "requestStatus.description", target = "requestStatus")
    @Mapping(source = "evaluationLog", target = "evaluationTotalTime", qualifiedByName = "calculateEvaluationTotalTime")
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
    protected void mapEvaluationMethodOptions(EvaluationRequest evaluationRequest,
                                              CrossValidationConfig crossValidationConfig,
                                              @MappingTarget EvaluationLog evaluationLog) {
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationRequest.getEvaluationMethod())) {
            evaluationLog.setNumFolds(
                    Optional.ofNullable(evaluationRequest.getNumFolds()).orElse(crossValidationConfig.getNumFolds()));
            evaluationLog.setNumTests(
                    Optional.ofNullable(evaluationRequest.getNumTests()).orElse(crossValidationConfig.getNumTests()));
            evaluationLog.setSeed(
                    Optional.ofNullable(evaluationRequest.getSeed()).orElse(crossValidationConfig.getSeed()));
        }
    }

    @AfterMapping
    protected void mapEvaluationMethodOptions(EvaluationLog evaluationLog,
                                              @MappingTarget EvaluationLogDto evaluationLogDto) {
        evaluationLogDto.setEvaluationMethod(new EnumDto(evaluationLog.getEvaluationMethod().name(),
                evaluationLog.getEvaluationMethod().getDescription()));
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationLog.getEvaluationMethod())) {
            evaluationLogDto.setNumFolds(evaluationLog.getNumFolds());
            evaluationLogDto.setNumTests(evaluationLog.getNumTests());
            evaluationLogDto.setSeed(evaluationLog.getSeed());
        }
    }

    @AfterMapping
    protected void mapEvaluationMethodOptions(EvaluationLog evaluationLog,
                                              @MappingTarget EvaluationLogBean evaluationLogBean) {
        String evaluationMethodDescription =
                getEvaluationMethodDescription(evaluationLog.getEvaluationMethod(), evaluationLog.getNumFolds(),
                        evaluationLog.getNumTests());
        evaluationLogBean.setEvaluationMethod(evaluationMethodDescription);
    }

    @AfterMapping
    protected void mapEvaluationStatus(EvaluationLog evaluationLog,
                                       @MappingTarget EvaluationLogDto evaluationLogDto) {
        evaluationLogDto.setRequestStatus(new EnumDto(evaluationLog.getRequestStatus().name(),
                evaluationLog.getRequestStatus().getDescription()));
    }
}
