package com.ecaservice.server.mapping;

import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.base.model.MessageError;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
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
     * Maps evaluation results data model to evaluation response.
     *
     * @param evaluationResultsDataModel - evaluation results data model
     * @return evaluation response
     */
    @Mapping(target = "modelUrl", ignore = true)
    @Mapping(target = "errors", ignore = true)
    EvaluationResponse map(EvaluationResultsDataModel evaluationResultsDataModel);

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
     * @param evaluationResultsDataModel - evaluation response data model
     * @param evaluationResponse          - evaluation response
     */
    @AfterMapping
    default void postMapping(EvaluationResultsDataModel evaluationResultsDataModel,
                             @MappingTarget EvaluationResponse evaluationResponse) {
        if (RequestStatus.FINISHED.equals(evaluationResultsDataModel.getStatus())) {
            evaluationResponse.setModelUrl(evaluationResultsDataModel.getModelUrl());
        } else if (RequestStatus.ERROR.equals(evaluationResultsDataModel.getStatus())) {
            MessageError error = error(evaluationResultsDataModel.getErrorCode());
            evaluationResponse.setErrors(Collections.singletonList(error));
        }
    }
}
