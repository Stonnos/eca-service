package com.ecaservice.web.push;

import com.ecaservice.web.dto.model.push.PushRequestDto;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.UUID;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    public static final String EXPERIMENT_STATUS = "EXPERIMENT_STATUS";

    private static final String TEST_REQUEST_JSON = "user-push-notification-test-request.json";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

    /**
     * Creates user push notification request.
     *
     * @return user push notification request
     */
    @SneakyThrows
    public static UserPushNotificationRequest createUserPushNotificationRequest() {
        @Cleanup InputStream inputStream =
                TestHelperUtils.class.getClassLoader().getResourceAsStream(TEST_REQUEST_JSON);
        return OBJECT_MAPPER.readValue(inputStream, UserPushNotificationRequest.class);
    }
}
