package com.ecaservice.mapping;

import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.ExperimentDto;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.List;

/**
 * Experiment mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class ExperimentMapper {

    /**
     * Maps experiment request to experiment persistence entity.
     *
     * @param experimentRequest experiment request
     * @return experiment entity
     */
    public abstract Experiment map(ExperimentRequest experimentRequest);

    /**
     * Maps experiment entity to experiment dto model.
     *
     * @param experiment - experiment entity
     * @return experiment dto model
     */
    @Mappings({
            @Mapping(source = "experimentAbsolutePath", target = "experimentAbsolutePath",
                    qualifiedByName = "toFileName"),
            @Mapping(source = "trainingDataAbsolutePath", target = "trainingDataAbsolutePath",
                    qualifiedByName = "toFileName"),
            @Mapping(target = "evaluationMethod", ignore = true),
            @Mapping(target = "experimentType", ignore = true),
            @Mapping(target = "experimentStatus", ignore = true)
    })
    public abstract ExperimentDto map(Experiment experiment);

    /**
     * Maps experiments entities to experiments dto models.
     *
     * @param experiments - experiments entities list
     * @return experiments dto models list
     */
    public abstract List<ExperimentDto> map(List<Experiment> experiments);

    @AfterMapping
    protected void postMapping(Experiment experiment, @MappingTarget ExperimentDto experimentDto) {
        experimentDto.setEvaluationMethod(new EnumDto(experiment.getEvaluationMethod().name(),
                experiment.getEvaluationMethod().getDescription()));
        experimentDto.setExperimentStatus(new EnumDto(experiment.getExperimentStatus().name(),
                experiment.getExperimentStatus().getDescription()));
        experimentDto.setExperimentType(new EnumDto(experiment.getExperimentType().name(),
                experiment.getExperimentType().getDescription()));
    }

    @Named("toFileName")
    protected String toFileName(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        } else {
            return FilenameUtils.getName(path);
        }
    }
}
