package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.report.model.ClassifierOptionsRequestBean;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
import com.ecaservice.web.dto.model.EnumDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.ecaservice.util.Utils.getEvaluationMethodDescription;

/**
 * Implements mapping classifier options request to classifier options request entity.
 *
 * @author Roman Batygin
 */
@Mapper(uses = {ErsEvaluationMethodMapper.class, ClassifierOptionsResponseModelMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ClassifierOptionsRequestModelMapper {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Maps classifier options request to classifier options request entity.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options request entity
     */
    @Mapping(source = "evaluationMethodReport.evaluationMethod", target = "evaluationMethod")
    @Mapping(source = "evaluationMethodReport.numFolds", target = "numFolds")
    @Mapping(source = "evaluationMethodReport.numTests", target = "numTests")
    @Mapping(source = "evaluationMethodReport.seed", target = "seed")
    public abstract ClassifierOptionsRequestModel map(ClassifierOptionsRequest classifierOptionsRequest);

    /**
     * Maps classifier options request model entity to its dto model.
     *
     * @param classifierOptionsRequestModel - classifier options request model entity
     * @return classifier options request dto model
     */
    @Mapping(target = "evaluationMethod", ignore = true)
    public abstract ClassifierOptionsRequestDto map(ClassifierOptionsRequestModel classifierOptionsRequestModel);

    /**
     * Maps classifier options request model entity to report bean model.
     *
     * @param classifierOptionsRequestModel - classifier options request model
     * @return classifier options request bean
     */
    @Mapping(target = "evaluationMethod", ignore = true)
    @Mapping(source = "requestDate", target = "requestDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(source = "responseStatus.description", target = "responseStatus")
    public abstract ClassifierOptionsRequestBean mapToBean(ClassifierOptionsRequestModel classifierOptionsRequestModel);

    /**
     * Maps classifier options request models entities to report beans models.
     *
     * @param classifierOptionsRequestModels - classifier options request models list
     * @return classifier options request beans list
     */
    public abstract List<ClassifierOptionsRequestBean> mapToBeans(
            List<ClassifierOptionsRequestModel> classifierOptionsRequestModels);

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

    @AfterMapping
    protected void mapEvaluationMethodOptions(ClassifierOptionsRequestModel requestModel, @MappingTarget
            ClassifierOptionsRequestBean classifierOptionsRequestBean) {
        String evaluationMethodDescription =
                getEvaluationMethodDescription(requestModel.getEvaluationMethod(), requestModel.getNumFolds(),
                        requestModel.getNumTests());
        classifierOptionsRequestBean.setEvaluationMethod(evaluationMethodDescription);
    }

    @AfterMapping
    protected void mapClassifierOptions(ClassifierOptionsRequestModel requestModel,
                                        @MappingTarget ClassifierOptionsRequestBean classifierOptionsRequestBean) {
        if (!CollectionUtils.isEmpty(requestModel.getClassifierOptionsResponseModels())) {
            ClassifierOptionsResponseModel classifierOptionsResponseModel =
                    requestModel.getClassifierOptionsResponseModels().iterator().next();
            classifierOptionsRequestBean.setClassifierName(classifierOptionsResponseModel.getClassifierName());
            classifierOptionsRequestBean.setOptimalClassifierOptions(classifierOptionsResponseModel.getOptions());
        }
    }

    @Named("formatLocalDateTime")
    protected String formatLocalDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime).map(dateTimeFormatter::format).orElse(null);
    }
}
