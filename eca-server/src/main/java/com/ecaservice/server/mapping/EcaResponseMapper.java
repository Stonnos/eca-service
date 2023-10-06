package com.ecaservice.server.mapping;

import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.base.model.MessageError;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.server.bpm.model.EvaluationResultsModel;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ValueMapping;

import java.util.Collections;

import static com.ecaservice.server.util.Utils.error;

/**
 * Experiment response mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface EcaResponseMapper {

    /**
     * Maps experiment to experiment response object.
     *
     * @param experiment experiment
     * @return experiment response model
     */
    @Mapping(source = "requestStatus", target = "status")
    @Mapping(target = "downloadUrl", ignore = true)
    @Mapping(target = "errors", ignore = true)
    ExperimentResponse map(Experiment experiment);

    /**
     * Maps evaluation results bpmn model to evaluation response.
     *
     * @param evaluationResultsModel - evaluation results model
     * @return evaluation response
     */
    @Mapping(source = "requestStatus", target = "status")
    @Mapping(target = "modelUrl", ignore = true)
    @Mapping(target = "errors", ignore = true)
    EvaluationResponse map(EvaluationResultsModel evaluationResultsModel);

    /**
     * Maps request status to technical status.
     *
     * @param requestStatus - request status
     * @return technical status
     */
    @ValueMapping(source = "NEW", target = "IN_PROGRESS")
    @ValueMapping(source = "IN_PROGRESS", target = "IN_PROGRESS")
    @ValueMapping(source = "FINISHED", target = "SUCCESS")
    @ValueMapping(source = "TIMEOUT", target = "TIMEOUT")
    @ValueMapping(source = "ERROR", target = "ERROR")
    TechnicalStatus map(RequestStatus requestStatus);

    /**
     * Post mapping data.
     *
     * @param experiment         - experiment entity
     * @param experimentResponse - experiment response
     */
    @AfterMapping
    default void postMapping(Experiment experiment, @MappingTarget ExperimentResponse experimentResponse) {
        if (RequestStatus.FINISHED.equals(experiment.getRequestStatus())) {
            experimentResponse.setDownloadUrl(experiment.getExperimentDownloadUrl());
        } else if (RequestStatus.ERROR.equals(experiment.getRequestStatus())) {
            MessageError error = error(ErrorCode.INTERNAL_SERVER_ERROR);
            experimentResponse.setErrors(Collections.singletonList(error));
        }
    }

    /**
     * Post mapping data.
     *
     * @param evaluationResultsModel - evaluation results model
     * @param evaluationResponse     - evaluation response
     */
    @AfterMapping
    default void postMapping(EvaluationResultsModel evaluationResultsModel,
                             @MappingTarget EvaluationResponse evaluationResponse) {
        if (RequestStatus.FINISHED.equals(evaluationResultsModel.getRequestStatus())) {
            evaluationResponse.setModelUrl(evaluationResultsModel.getModelUrl());
        } else if (RequestStatus.ERROR.equals(evaluationResultsModel.getRequestStatus())) {
            MessageError error = error(ErrorCode.INTERNAL_SERVER_ERROR);
            evaluationResponse.setErrors(Collections.singletonList(error));
        }
    }
}
