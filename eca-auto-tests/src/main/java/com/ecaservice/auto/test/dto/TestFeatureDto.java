package com.ecaservice.auto.test.dto;

import com.ecaservice.auto.test.entity.autotest.TestFeature;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Test feature dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Test feature dto")
public class TestFeatureDto {

    /**
     * Feature description
     */
    @Schema(description = "Feature type")
    private TestFeature feature;

    /**
     * Feature type
     */
    @Schema(description = "Feature description")
    private String featureDescription;
}
