package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Instances info details dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Instances info details model")
public class InstancesInfoDetailsDto extends InstancesInfoDto {

    /**
     * Attributes list
     */
    @Schema(description = "Attributes list")
    private List<AttributeMetaInfoDto> attributes;
}
