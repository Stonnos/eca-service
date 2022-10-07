package com.ecaservice.web.push.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * System push request.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "System push request")
public class SystemPushRequest extends AbstractPushRequest {

    /**
     * Show push message?
     */
    @Schema(description = "Show push message?")
    private boolean showMessage;
}
