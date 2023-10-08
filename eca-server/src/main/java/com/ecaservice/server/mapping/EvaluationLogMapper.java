package com.ecaservice.server.mapping;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.server.bpm.model.EvaluationLogModel;
import com.ecaservice.server.bpm.model.EvaluationRequestModel;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.dto.CreateEvaluationRequestDto;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.evaluation.EvaluationRequestData;
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
import org.springframework.amqp.core.Message;

import java.util.Optional;

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
    @Mapping(target = "channel", constant = "QUEUE")
    public abstract EvaluationRequestModel map(EvaluationRequest evaluationRequest);

    /**
     * Maps instances request to evaluation request internal data model.
     *
     * @param instancesRequest - instances request
     * @return evaluation request internal data model
     */
    @Mapping(source = "instancesRequest.dataUuid", target = "dataUuid")
    @Mapping(source = "message.messageProperties.replyTo", target = "replyTo")
    @Mapping(source = "message.messageProperties.correlationId", target = "correlationId")
    @Mapping(source = "crossValidationConfig.numFolds", target = "numFolds")
    @Mapping(source = "crossValidationConfig.numTests", target = "numTests")
    @Mapping(source = "crossValidationConfig.seed", target = "seed")
    @Mapping(target = "channel", constant = "QUEUE")
    @Mapping(target = "evaluationMethod", constant = "CROSS_VALIDATION")
    @Mapping(target = "useOptimalClassifierOptions", constant = "true")
    public abstract EvaluationRequestModel map(InstancesRequest instancesRequest,
                                               Message message,
                                               CrossValidationConfig crossValidationConfig);

    /**
     * Maps evaluation request to evaluation web request internal data model.
     *
     * @param createEvaluationRequestDto - evaluation request
     * @return evaluation web request internal data model
     */
    @Mapping(source = "instancesUuid", target = "dataUuid")
    public abstract EvaluationRequestModel map(CreateEvaluationRequestDto createEvaluationRequestDto);

    /**
     * Maps evaluation request model to evaluation request data.
     *
     * @param evaluationRequestModel - evaluation request model
     * @return evaluation request data
     */
    public abstract EvaluationRequestData map(EvaluationRequestModel evaluationRequestModel);

    /**
     * Maps evaluation request to evaluation log.
     *
     * @param evaluationRequest     - evaluation request model
     * @param crossValidationConfig - cross validation config
     * @return evaluation log entity
     */
    @Mapping(source = "evaluationRequest.classifier", target = "classifierInfo")
    @Mapping(source = "evaluationRequest.dataUuid", target = "trainingDataUuid")
    @Mapping(target = "numFolds", ignore = true)
    @Mapping(target = "numTests", ignore = true)
    @Mapping(target = "seed", ignore = true)
    public abstract EvaluationLog map(EvaluationRequestData evaluationRequest,
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

    /**
     * Maps evaluation log entity to bpmn model.
     *
     * @param evaluationLog - evaluation log entity
     * @return evaluation log bpmn model
     */
    @Mapping(source = "classifierInfo.classifierName", target = "classifierName")
    public abstract EvaluationLogModel mapToModel(EvaluationLog evaluationLog);

    @AfterMapping
    protected void mapEvaluationMethodOptions(EvaluationRequestData evaluationRequest,
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
