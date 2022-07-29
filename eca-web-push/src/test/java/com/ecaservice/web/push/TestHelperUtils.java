package com.ecaservice.web.push;

import com.ecaservice.web.dto.model.push.PushRequestDto;
import lombok.experimental.UtilityClass;

import java.util.UUID;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    public static final String EXPERIMENT_STATUS = "EXPERIMENT_STATUS";

    /**
     * Creates push request dto.
     *
     * @return push request dto
     */
    public static PushRequestDto createPushRequestDto() {
        var pushRequestDto = new PushRequestDto();
        pushRequestDto.setRequestId(UUID.randomUUID().toString());
        pushRequestDto.setMessageType(EXPERIMENT_STATUS);
        return pushRequestDto;
    }
}
