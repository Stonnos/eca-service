package com.ecaservice.server.mapping;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataModel;
import com.ecaservice.server.report.model.EvaluationLogBean;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import eca.core.evaluation.EvaluationMethod;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Optional;

import static com.ecaservice.server.util.InstancesUtils.md5Hash;
import static com.ecaservice.server.util.Utils.getEvaluationMethodDescription;

/**
 * Implements evaluation request to evaluation log mapping.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {InstancesInfoMapper.class, ClassifierInfoMapper.class, DateTimeConverter.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class EvaluationLogMapper extends AbstractEvaluationMapper {

    /**
     * Maps evaluation request to evaluation request internal data model.
     *
     * @param evaluationRequest - evaluation request
     * @return evaluation request internal data model
     */
    public abstract EvaluationRequestDataModel map(EvaluationRequest evaluationRequest);

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
    public abstract EvaluationLog map(EvaluationRequestDataModel evaluationRequest,
                                      CrossValidationConfig crossValidationConfig);

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
    @Mapping(target = "classifierInfo", ignore = true)
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
    @Mapping(target = "classifierInfo", ignore = true)
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
    @Mapping(source = "instancesInfo.relationName", target = "relationName")
    @Mapping(target = "classifierName", ignore = true)
    public abstract EvaluationLogBean mapToBean(EvaluationLog evaluationLog);

    @AfterMapping
    protected void mapEvaluationMethodOptions(EvaluationRequestDataModel evaluationRequest,
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

    @AfterMapping
    protected void mapDataMd5Hash(EvaluationRequest evaluationRequest,
                             @MappingTarget EvaluationRequestDataModel evaluationRequestDataModel) {
        evaluationRequestDataModel.setDataMd5Hash(md5Hash(evaluationRequest.getData()));
    }
}
