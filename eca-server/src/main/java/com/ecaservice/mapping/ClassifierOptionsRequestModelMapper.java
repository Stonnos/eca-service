package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
import com.ecaservice.web.dto.model.EnumDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * Implements mapping classifier options request to classifier options request entity.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {ErsEvaluationMethodMapper.class, ClassifierOptionsResponseModelMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ClassifierOptionsRequestModelMapper {

    /**
     * Maps classifier options request to classifier options request entity.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options request entity
     */
    @Mappings({
            @Mapping(source = "evaluationMethodReport.evaluationMethod", target = "evaluationMethod"),
            @Mapping(source = "evaluationMethodReport.numFolds", target = "numFolds"),
            @Mapping(source = "evaluationMethodReport.numTests", target = "numTests"),
            @Mapping(source = "evaluationMethodReport.seed", target = "seed"),
    })
    public abstract ClassifierOptionsRequestModel map(ClassifierOptionsRequest classifierOptionsRequest);

    /**
     * Maps classifier options request model entity to its dto model.
     *
     * @param classifierOptionsRequestModel - classifier options request model entity
     * @return classifier options request dto model
     */
    @Mappings({
            @Mapping(target = "evaluationMethod", ignore = true)
    })
    public abstract ClassifierOptionsRequestDto map(ClassifierOptionsRequestModel classifierOptionsRequestModel);

    /**
     * Maps classifiers options requests models entities to its dto models.
     *
     * @param classifierOptionsRequestModels - classifiers options requests models entities
     * @return classifiers options requests dto models
     */
    public abstract List<ClassifierOptionsRequestDto> map(
            List<ClassifierOptionsRequestModel> classifierOptionsRequestModels);

    @AfterMapping
    protected void mapResponseStatus(ClassifierOptionsRequestModel classifierOptionsRequestModel,
                                     @MappingTarget ClassifierOptionsRequestDto classifierOptionsRequestDto) {
        classifierOptionsRequestDto.setResponseStatus(
                new EnumDto(classifierOptionsRequestModel.getResponseStatus().name(),
                        classifierOptionsRequestModel.getResponseStatus().getDescription()));
    }

    @AfterMapping
    protected void mapEvaluationMethod(ClassifierOptionsRequestModel classifierOptionsRequestModel,
                                       @MappingTarget ClassifierOptionsRequestDto classifierOptionsRequestDto) {
        classifierOptionsRequestDto.setEvaluationMethod(
                new EnumDto(classifierOptionsRequestModel.getEvaluationMethod().name(),
                        classifierOptionsRequestModel.getEvaluationMethod().getDescription()));
    }
}
