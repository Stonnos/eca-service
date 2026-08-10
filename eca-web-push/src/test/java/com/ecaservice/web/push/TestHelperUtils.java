package com.ecaservice.web.push;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UserNotificationDto;
import com.ecaservice.web.push.config.MailProperties;
import com.ecaservice.web.push.dto.SystemPushRequest;
import com.ecaservice.web.push.dto.UpdateUserNotificationEventOptionsDto;
import com.ecaservice.web.push.dto.UpdateUserNotificationOptionsDto;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.entity.Email;
import com.ecaservice.web.push.entity.EmailStatus;
import com.ecaservice.web.push.entity.MessageStatus;
import com.ecaservice.web.push.entity.RegexEntity;
import com.ecaservice.web.push.entity.TemplateEntity;
import com.ecaservice.web.push.entity.TemplateParameterEntity;
import com.ecaservice.web.push.entity.UserNotificationEntity;
import com.ecaservice.web.push.entity.NotificationEventOptionsEntity;
import com.ecaservice.web.push.entity.NotificationEventType;
import com.ecaservice.web.push.entity.NotificationOptionsEntity;
import com.ecaservice.web.push.entity.NotificationParameter;
import com.ecaservice.web.push.entity.PushTokenEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.notification.util.Priority.LOW;
import static com.ecaservice.notification.util.Priority.MEDIUM;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

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
    private static final String MESSAGE = "message";
    private static final String SUBJECT = "subject";
    private static final String SENDER_MAIL_RU = "sender@mail.ru";
    private static final String RECEIVER_MAIL_RU = "receiver@mail.ru";
    private static final String TEST_TEMPLATE_CODE = "testTemplate";
    private static final String TEMPLATE_CODE = "testTemplate";
    private static final String TEMPLATE_DESCRIPTION = "Test template";
    private static final String TEST_REGEX = "testRegex";
    private static final String REGEX_DESCRIPTION = "Test regex";
    private static final int PAGE = 0;
    private static final int SIZE = 10;

    /**
     * Creates email request.
     *
     * @return email request
     */
    public static EmailRequest createEmailRequest() {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setRequestId(UUID.randomUUID().toString());
        emailRequest.setCorrelationId(UUID.randomUUID().toString());
        emailRequest.setReceiver(RECEIVER_MAIL_RU);
        emailRequest.setTemplateCode(TEST_TEMPLATE_CODE);
        emailRequest.setVariables(newHashMap());
        emailRequest.setPriority(LOW);
        return emailRequest;
    }

    /**
     * Create mail config.
     *
     * @return mail config
     */
    public static MailProperties createMailConfig() {
        MailProperties mailProperties = new MailProperties();
        mailProperties.setSender(SENDER_MAIL_RU);
        return mailProperties;
    }

    /**
     * Creates email.
     *
     * @param saveDate - save date
     * @param status   - email status
     * @return email object
     */
    public static Email createEmail(LocalDateTime saveDate, EmailStatus status) {
        Email email = new Email();
        email.setUuid(UUID.randomUUID().toString());
        email.setSaveDate(saveDate);
        email.setStatus(status);
        email.setSender(SENDER_MAIL_RU);
        email.setReceiver(RECEIVER_MAIL_RU);
        email.setSubject(SUBJECT);
        email.setMessage(MESSAGE);
        email.setPriority(MEDIUM);
        return email;
    }

    /**
     * Creates template entity.
     *
     * @return template entity
     */
    public static TemplateEntity createTemplateEntity() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setCode(TEMPLATE_CODE);
        templateEntity.setDescription(TEMPLATE_DESCRIPTION);
        templateEntity.setBody(MESSAGE);
        templateEntity.setSubject(SUBJECT);
        templateEntity.setCreated(LocalDateTime.now());
        templateEntity.setParameters(newArrayList());
        return templateEntity;
    }

    /**
     * Creates template entity.
     *
     * @param code - template code
     * @return template entity
     */
    public static TemplateEntity createTemplateEntity(String code) {
        var templateEntity = createTemplateEntity();
        templateEntity.setCode(code);
        return templateEntity;
    }

    /**
     * Creates template parameter entity.
     *
     * @param name - parameter name
     * @return template parameter entity
     */
    public static TemplateParameterEntity createTemplateParameterEntity(String name) {
        TemplateParameterEntity templateParameterEntity = new TemplateParameterEntity();
        templateParameterEntity.setParameterName(name);
        templateParameterEntity.setCreated(LocalDateTime.now());
        return templateParameterEntity;
    }

    /**
     * Creates regex entity.
     *
     * @param value - regex string
     * @return regex entity
     */
    public static RegexEntity createRegex(String value) {
        RegexEntity regex = new RegexEntity();
        regex.setRegexCode(TEST_REGEX);
        regex.setDescription(REGEX_DESCRIPTION);
        regex.setRegex(value);
        regex.setCreated(LocalDateTime.now());
        return regex;
    }

    /**
     * Creates page request dto.
     *
     * @return page request dto
     */
    public static PageRequestDto createPageRequestDto() {
        return new PageRequestDto(PAGE, SIZE, Collections.emptyList(), null, Collections.emptyList());
    }

    /**
     * Creates system push request dto.
     *
     * @return push request dto
     */
    public static SystemPushRequest createSystemPushRequest() {
        var pushRequestDto = new SystemPushRequest();
        pushRequestDto.setRequestId(UUID.randomUUID().toString());
        pushRequestDto.setCorrelationId(UUID.randomUUID().toString());
        pushRequestDto.setMessageType(EXPERIMENT_STATUS);
        pushRequestDto.setAdditionalProperties(Collections.singletonMap("key", "value"));
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
    public static UserNotificationEntity createNotificationEntity(String receiver,
                                                                  MessageStatus messageStatus,
                                                                  LocalDateTime created) {
        UserNotificationEntity userNotificationEntity = new UserNotificationEntity();
        userNotificationEntity.setCreated(created);
        userNotificationEntity.setMessageStatus(messageStatus);
        userNotificationEntity.setReceiver(receiver);
        userNotificationEntity.setInitiator(INITIATOR);
        userNotificationEntity.setMessageType(EXPERIMENT_STATUS);
        userNotificationEntity.setMessageText(MESSAGE_TEXT);
        userNotificationEntity.setParameters(Collections.singletonList(createNotificationParameter(PARAM, VALUE)));
        return userNotificationEntity;
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
    public static UserNotificationEntity createNotificationEntity() {
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

    /**
     * Creates push token entity.
     *
     * @param user     - user
     * @param tokenId  - token id
     * @param expireAt - expire at date
     * @return push token entity
     */
    public static PushTokenEntity createPushTokenEntity(String user, String tokenId, LocalDateTime expireAt) {
        var pushTokenEntity = new PushTokenEntity();
        pushTokenEntity.setUser(user);
        pushTokenEntity.setTokenId(tokenId);
        pushTokenEntity.setExpireAt(expireAt);
        return pushTokenEntity;
    }

    /**
     * Create notification options entity.
     *
     * @param user - user
     * @return notification options entity
     */
    public static NotificationOptionsEntity createUserProfileOptionsEntity(String user) {
        var userProfileOptionsEntity = new NotificationOptionsEntity();
        userProfileOptionsEntity.setEmailEnabled(true);
        userProfileOptionsEntity.setWebPushEnabled(true);
        userProfileOptionsEntity.setUser(user);
        userProfileOptionsEntity.setNotificationEventOptions(
                Collections.singletonList(createUserNotificationEventOptionsEntity(userProfileOptionsEntity)));
        userProfileOptionsEntity.setCreated(LocalDateTime.now());
        return userProfileOptionsEntity;
    }

    /**
     * Creates user notification event options entity.
     *
     * @param notificationOptionsEntity - user profile options entity
     * @return user notification event options entity
     */
    public static NotificationEventOptionsEntity createUserNotificationEventOptionsEntity(
            NotificationOptionsEntity notificationOptionsEntity) {
        var userNotificationEventOptionsEntity = new NotificationEventOptionsEntity();
        userNotificationEventOptionsEntity.setEventType(NotificationEventType.EXPERIMENT_STATUS_CHANGE);
        userNotificationEventOptionsEntity.setEmailSupported(true);
        userNotificationEventOptionsEntity.setWebPushSupported(true);
        userNotificationEventOptionsEntity.setEmailEnabled(true);
        userNotificationEventOptionsEntity.setEmailSupported(true);
        userNotificationEventOptionsEntity.setNotificationOptions(notificationOptionsEntity);
        return userNotificationEventOptionsEntity;
    }

    /**
     * Creates update user notification options.
     *
     * @return update user notification options
     */
    public static UpdateUserNotificationOptionsDto createUpdateUserNotificationOptionsDto() {
        var userNotificationOptionsDto = new UpdateUserNotificationOptionsDto();
        userNotificationOptionsDto.setWebPushEnabled(false);
        userNotificationOptionsDto.setEmailEnabled(false);
        userNotificationOptionsDto.setNotificationEventOptions(newArrayList());
        userNotificationOptionsDto.getNotificationEventOptions().add(
                createUpdateUserNotificationEventOptionsDto(NotificationEventType.EXPERIMENT_STATUS_CHANGE));
        userNotificationOptionsDto.getNotificationEventOptions().add(
                createUpdateUserNotificationEventOptionsDto(NotificationEventType.CLASSIFIER_STATUS_CHANGE));
        return userNotificationOptionsDto;
    }

    /**
     * Creates update user notification event options dto.
     *
     * @param eventType - event type
     * @return update user notification event options dto
     */
    public static UpdateUserNotificationEventOptionsDto createUpdateUserNotificationEventOptionsDto(
            NotificationEventType eventType) {
        var updateUserNotificationEventOptionsDto = new UpdateUserNotificationEventOptionsDto();
        updateUserNotificationEventOptionsDto.setEventType(eventType);
        updateUserNotificationEventOptionsDto.setEmailEnabled(false);
        updateUserNotificationEventOptionsDto.setWebPushEnabled(false);
        return updateUserNotificationEventOptionsDto;
    }
}
