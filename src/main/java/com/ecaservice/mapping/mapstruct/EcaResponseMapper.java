package com.ecaservice.mapping.mapstruct;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.Arrays;
import java.util.Collection;

/**
 * Eca response mapper.
 * @author Roman Batygin
 */
@Mapper
public abstract class EcaResponseMapper {

    private static final Collection<ExperimentStatus> SUCCESS_STATUSES =
            Arrays.asList(ExperimentStatus.NEW, ExperimentStatus.FINISHED);

    @Mappings({
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "errorMessage", ignore = true)
    })
    public abstract EcaResponse map(Experiment experiment);

    @AfterMapping
    protected void mapTechnicalStatus(Experiment experiment, @MappingTarget EcaResponse response) {
        if (SUCCESS_STATUSES.contains(experiment.getExperimentStatus())) {
            response.setStatus(TechnicalStatus.SUCCESS);
        } else {
            response.setStatus(TechnicalStatus.ERROR);
            response.setErrorMessage(experiment.getErrorMessage());
        }
    }
}
