package com.ecaservice.mapping;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Roman Batygin
 */
@Component
public class ExperimentToEcaResponseConverter extends CustomConverter<Experiment, EcaResponse> {

    private static final Collection<ExperimentStatus> SUCCESS_STATUSES =
            Arrays.asList(ExperimentStatus.NEW, ExperimentStatus.FINISHED);

    @Override
    public EcaResponse convert(Experiment experiment, Type<? extends EcaResponse> type) {
        EcaResponse response = new EcaResponse();
        if (SUCCESS_STATUSES.contains(experiment.getExperimentStatus())) {
            response.setStatus(TechnicalStatus.SUCCESS);
        } else {
            response.setStatus(TechnicalStatus.ERROR);
            response.setErrorMessage(experiment.getErrorMessage());
        }
        return response;
    }
}
