package com.ecaservice.web.push.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * System push request.
 *
 * @author Roman Batygin
 */
@Getter
@Setter
@Schema(description = "System push request")
public class SystemPushRequest extends AbstractPushRequest {

    /**
     * Creates system push request
     */
    public SystemPushRequest() {
        super(PushType.SYSTEM);
    }

    /**
     * Show push message?
     */
    @Schema(description = "Show push message?")
    private boolean showMessage;
}
