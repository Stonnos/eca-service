package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Chart model dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Chart model")
public class ChartDto {

    /**
     * Total items
     */
    @Schema(description = "Total items", required = true, example = "10", minimum = ZERO_VALUE_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private Long total;

    /**
     * Chart data items
     */
    @Schema(description = "Chart data items")
    private List<ChartDataDto> dataItems;
}
