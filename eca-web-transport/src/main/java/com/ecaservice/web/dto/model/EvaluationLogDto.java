package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Evaluation log dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Classifier evaluation log model")
public class EvaluationLogDto extends AbstractEvaluationDto {

    /**
     * Classifier info
     */
    @ApiModelProperty(value = "Classifier info")
    private ClassifierInfoDto classifierInfo;

    /**
     * Training data info
     */
    @ApiModelProperty(value = "Training data info")
    private InstancesInfoDto instancesInfo;
}