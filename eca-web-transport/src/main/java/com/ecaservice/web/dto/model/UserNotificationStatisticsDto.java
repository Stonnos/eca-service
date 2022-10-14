package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * User notification statistics dto model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "User notification statistics model")
public class UserNotificationStatisticsDto {

    /**
     * Not read count for last N days
     */
    @Schema(description = "Not read count for last N days", example = "10", minimum = ZERO_VALUE_STRING,
            maximum = MAX_LONG_VALUE_STRING)
    private long notReadCount;
}
