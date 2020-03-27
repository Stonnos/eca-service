package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Experiment results sending status model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Experiment results sending status model")
@AllArgsConstructor
public class SendingStatus {

    /**
     * Experiment request id
     */
    @ApiModelProperty(value = "Experiment request id")
    private String experimentRequestId;

    /**
     * Sending status
     */
    @ApiModelProperty(value = "Sending status")
    private boolean sending;
}