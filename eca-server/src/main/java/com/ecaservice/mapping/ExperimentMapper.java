package com.ecaservice.mapping;

import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.report.model.ExperimentBean;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.ExperimentDto;
import eca.core.evaluation.EvaluationMethod;
import org.apache.commons.io.FilenameUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Experiment mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class ExperimentMapper {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Maps experiment request to experiment persistence entity.
     *
     * @param experimentRequest     - experiment request
     * @param crossValidationConfig - cross - validation config
     * @return experiment entity
     */
    @Mapping(target = "numFolds", ignore = true)
    @Mapping(target = "numTests", ignore = true)
    @Mapping(target = "seed", ignore = true)
    public abstract Experiment map(ExperimentRequest experimentRequest, CrossValidationConfig crossValidationConfig);

    /**
     * Maps experiment entity to experiment dto model.
     *
     * @param experiment - experiment entity
     * @return experiment dto model
     */
    @Mapping(source = "experimentAbsolutePath", target = "experimentAbsolutePath", qualifiedByName = "toFileName")
    @Mapping(source = "trainingDataAbsolutePath", target = "trainingDataAbsolutePath", qualifiedByName = "toFileName")
    @Mapping(target = "evaluationMethod", ignore = true)
    @Mapping(target = "experimentType", ignore = true)
    @Mapping(target = "experimentStatus", ignore = true)
    public abstract ExperimentDto map(Experiment experiment);

    /**
     * Maps experiments entities to experiments dto models.
     *
     * @param experiments - experiments entities list
     * @return experiments dto models list
     */
    public abstract List<ExperimentDto> map(List<Experiment> experiments);

    /**
     * Maps experiment entity to experiment report bean.
     *
     * @param experiment - experiment entity
     * @return experiment bean
     */
    @Mapping(source = "experimentAbsolutePath", target = "experimentAbsolutePath", qualifiedByName = "toFileName")
    @Mapping(source = "trainingDataAbsolutePath", target = "trainingDataAbsolutePath", qualifiedByName = "toFileName")
    @Mapping(source = "evaluationMethod.description", target = "evaluationMethod")
    @Mapping(source = "experimentType.description", target = "experimentType")
    @Mapping(source = "experimentStatus.description", target = "experimentStatus")
    @Mapping(source = "creationDate", target = "creationDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "sentDate", target = "sentDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "deletedDate", target = "deletedDate", qualifiedByName = "formatLocalDateTime")
    public abstract ExperimentBean mapToBean(Experiment experiment);

    /**
     * Maps experiments entities list to experiments report beans.
     *
     * @param experiments - experiment entities list
     * @return experiments beans list
     */
    public abstract List<ExperimentBean> mapToBeans(List<Experiment> experiments);

    @AfterMapping
    protected void mapEvaluationMethodOptions(ExperimentRequest experimentRequest,
                                              CrossValidationConfig crossValidationConfig,
                                              @MappingTarget Experiment experiment) {
        if (EvaluationMethod.CROSS_VALIDATION.equals(experiment.getEvaluationMethod())) {
            experiment.setNumFolds(crossValidationConfig.getNumFolds());
            experiment.setNumTests(crossValidationConfig.getNumTests());
            experiment.setSeed(crossValidationConfig.getSeed());
        }
    }

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
        return FilenameUtils.getName(path);
    }

    @Named("formatLocalDateTime")
    protected String formatLocalDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(dateTimeFormatter::format).orElse(null);
    }
}
