package com.ecaservice.data.loader.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * Instances meta info dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Instances meta info dto")
public class InstancesMetaInfoDto {

    /**
     * Instances uuid
     */
    @Schema(description = "Instances uuid", example = "f8cecbf7-405b-403b-9a94-f51e8fb73ed8", maxLength = FieldConstraints.UUID_MAX_SIZE)
    private String uuid;

    /**
     * Instances name
     */
    @Schema(description = "Instances name", example = "iris", maxLength = FieldConstraints.MAX_LENGTH_255)
    private String relationName;

    /**
     * Instances size
     */
    @Schema(description = "Instances number", example = "150", minimum = FieldConstraints.ZERO_VALUE_STRING,
            maximum = FieldConstraints.MAX_INTEGER_VALUE_STRING)
    private Integer numInstances;

    /**
     * Attributes number
     */
    @Schema(description = "Attributes number", example = "5", minimum = FieldConstraints.ZERO_VALUE_STRING,
            maximum = FieldConstraints.MAX_INTEGER_VALUE_STRING)
    private Integer numAttributes;

    /**
     * Class name
     */
    @Schema(description = "Class name", example = "class", maxLength = FieldConstraints.MAX_LENGTH_255)
    private String className;

    /**
     * Instances file MD5 hash sum
     */
    @Schema(description = "Instances file MD5 hash sum", example = "3032e188204cb537f69fc7364f638641",
            maxLength = FieldConstraints.MAX_LENGTH_255)
    private String md5Hash;

    /**
     * Instances object path in storage
     */
    @Schema(description = "Instances object path in storage",
            example = "instances-f8cecbf7-405b-403b-9a94-f51e8fb73ed8.json", maxLength = FieldConstraints.MAX_LENGTH_255)
    private String objectPath;
}
