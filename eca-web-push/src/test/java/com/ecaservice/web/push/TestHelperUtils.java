package com.ecaservice.web.push;

import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.UserNotificationDto;
import com.ecaservice.web.dto.model.push.PushRequestDto;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.entity.MessageStatus;
import com.ecaservice.web.push.entity.NotificationEntity;
import com.ecaservice.web.push.entity.NotificationParameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
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
    private static final String MESSAGE_TEXT = "Message text";
    private static final String INITIATOR = "initiator";
    private static final String RECEIVER = "receiver";
    private static final String VALUE = "Value";
    private static final String PARAM = "Param";
    private static final long ID = 1L;

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

    /**
     * Creates notification entity.
     *
     * @param receiver      - receiver
     * @param messageStatus - message status
     * @param created       - created date
     * @return notification entity
     */
    public static NotificationEntity createNotificationEntity(String receiver,
                                                              MessageStatus messageStatus,
                                                              LocalDateTime created) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setCreated(created);
        notificationEntity.setMessageStatus(messageStatus);
        notificationEntity.setReceiver(receiver);
        notificationEntity.setInitiator(INITIATOR);
        notificationEntity.setMessageType(EXPERIMENT_STATUS);
        notificationEntity.setMessageText(MESSAGE_TEXT);
        notificationEntity.setParameters(Collections.singletonList(createNotificationParameter(PARAM, VALUE)));
        return notificationEntity;
    }

    /**
     * Creates notification parameter.
     *
     * @param name  - parameter name
     * @param value - parameter value
     * @return notification parameter
     */
    public static NotificationParameter createNotificationParameter(String name, String value) {
        var notificationParameter = new NotificationParameter();
        notificationParameter.setName(name);
        notificationParameter.setValue(value);
        return notificationParameter;
    }

    /**
     * Creates notification entity.
     *
     * @return notification entity
     */
    public static NotificationEntity createNotificationEntity() {
        return createNotificationEntity(RECEIVER, MessageStatus.NOT_READ, LocalDateTime.now());
    }

    /**
     * Creates user notification dto.
     *
     * @return user notification dto
     */
    public static UserNotificationDto createUserNotificationDto() {
        UserNotificationDto userNotificationDto = new UserNotificationDto();
        userNotificationDto.setCreated(LocalDateTime.now());
        userNotificationDto.setInitiator(INITIATOR);
        userNotificationDto.setMessageText(MESSAGE_TEXT);
        userNotificationDto.setMessageType(EXPERIMENT_STATUS);
        userNotificationDto.setId(ID);
        userNotificationDto.setMessageStatus(
                new EnumDto(MessageStatus.NOT_READ.name(), MessageStatus.NOT_READ.getDescription()));
        return userNotificationDto;
    }
}
