package com.ecaservice.mapping;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.report.model.ExperimentBean;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.ExperimentDto;
import eca.core.evaluation.EvaluationMethod;
import org.apache.commons.io.FilenameUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

import static com.ecaservice.util.Utils.getEvaluationMethodDescription;

/**
 * Experiment mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = DateTimeConverter.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ExperimentMapper extends AbstractEvaluationMapper {

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
    @Mapping(source = "experiment", target = "evaluationTotalTime", qualifiedByName = "calculateEvaluationTotalTime")
    @Mapping(target = "evaluationMethod", ignore = true)
    @Mapping(target = "experimentType", ignore = true)
    @Mapping(target = "requestStatus", ignore = true)
    @Mapping(target = "numFolds", ignore = true)
    @Mapping(target = "numTests", ignore = true)
    @Mapping(target = "seed", ignore = true)
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
    @Mapping(source = "experiment", target = "evaluationTotalTime", qualifiedByName = "calculateEvaluationTotalTime")
    @Mapping(source = "experimentAbsolutePath", target = "experimentAbsolutePath", qualifiedByName = "toFileName")
    @Mapping(source = "trainingDataAbsolutePath", target = "trainingDataAbsolutePath", qualifiedByName = "toFileName")
    @Mapping(target = "evaluationMethod", ignore = true)
    @Mapping(source = "experimentType.description", target = "experimentType")
    @Mapping(source = "requestStatus.description", target = "requestStatus")
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
    protected void mapEvaluationMethodOptions(CrossValidationConfig crossValidationConfig,
                                              @MappingTarget Experiment experiment) {
        if (EvaluationMethod.CROSS_VALIDATION.equals(experiment.getEvaluationMethod())) {
            experiment.setNumFolds(crossValidationConfig.getNumFolds());
            experiment.setNumTests(crossValidationConfig.getNumTests());
            experiment.setSeed(crossValidationConfig.getSeed());
        }
    }

    @AfterMapping
    protected void postMappingExperimentRequest(ExperimentRequest experimentRequest,
                                                @MappingTarget Experiment experiment) {
       experiment.setClassIndex(experimentRequest.getData().classIndex());
    }

    @AfterMapping
    protected void mapEvaluationMethodOptions(Experiment experiment, @MappingTarget ExperimentDto experimentDto) {
        experimentDto.setEvaluationMethod(new EnumDto(experiment.getEvaluationMethod().name(),
                experiment.getEvaluationMethod().getDescription()));
        if (EvaluationMethod.CROSS_VALIDATION.equals(experiment.getEvaluationMethod())) {
            experimentDto.setNumFolds(experiment.getNumFolds());
            experimentDto.setNumTests(experiment.getNumTests());
            experimentDto.setSeed(experiment.getSeed());
        }
    }

    @AfterMapping
    protected void mapEvaluationMethodOptions(Experiment experiment, @MappingTarget ExperimentBean experimentBean) {
        String evaluationMethodDescription =
                getEvaluationMethodDescription(experiment.getEvaluationMethod(), experiment.getNumFolds(),
                        experiment.getNumTests());
        experimentBean.setEvaluationMethod(evaluationMethodDescription);
    }

    @AfterMapping
    protected void postMapping(Experiment experiment, @MappingTarget ExperimentDto experimentDto) {
        experimentDto.setRequestStatus(new EnumDto(experiment.getRequestStatus().name(),
                experiment.getRequestStatus().getDescription()));
        experimentDto.setExperimentType(new EnumDto(experiment.getExperimentType().name(),
                experiment.getExperimentType().getDescription()));
    }

    @Named("toFileName")
    protected String toFileName(String path) {
        return FilenameUtils.getName(path);
    }
}
