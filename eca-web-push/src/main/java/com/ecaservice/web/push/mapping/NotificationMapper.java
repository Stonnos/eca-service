package com.ecaservice.web.push.mapping;

import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.UserNotificationDto;
import com.ecaservice.web.dto.model.push.PushRequestDto;
import com.ecaservice.web.push.dto.SystemPushRequest;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.entity.NotificationEntity;
import com.ecaservice.web.push.entity.NotificationParameter;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Notification mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface NotificationMapper {

    /**
     * Maps push notification request to entity model.
     *
     * @param userPushNotificationRequest - user push notification request
     * @return notification entity
     */
    NotificationEntity map(UserPushNotificationRequest userPushNotificationRequest);

    /**
     * Maps user notification entity to dto model.
     *
     * @param notificationEntity - notification entity
     * @return user notification dto
     */
    @Mapping(target = "messageStatus", ignore = true)
    UserNotificationDto map(NotificationEntity notificationEntity);

    /**
     * Maps user notifications entities to dto list.
     *
     * @param notifications - user notifications entities
     * @return user notifications dto list
     */
    List<UserNotificationDto> map(List<NotificationEntity> notifications);

    PushRequestDto map(SystemPushRequest systemPushRequest);

    /**
     * Maps notification parameters
     *
     * @param userPushNotificationRequest - user push notification request
     * @param notificationEntity          - notification entity
     */
    @AfterMapping
    default void mapParameters(UserPushNotificationRequest userPushNotificationRequest,
                               @MappingTarget NotificationEntity notificationEntity) {
        if (!CollectionUtils.isEmpty(userPushNotificationRequest.getAdditionalProperties())) {
            var parameters = userPushNotificationRequest.getAdditionalProperties().entrySet()
                    .stream()
                    .map(entry -> {
                        var notificationParameter = new NotificationParameter();
                        notificationParameter.setName(entry.getKey());
                        notificationParameter.setValue(entry.getValue());
                        return notificationParameter;
                    })
                    .collect(Collectors.toList());
            notificationEntity.setParameters(parameters);
        }
    }

    /**
     * Maps message status.
     *
     * @param notificationEntity  - notification entity
     * @param userNotificationDto - user notification dto
     */
    @AfterMapping
    default void mapMessageStatus(NotificationEntity notificationEntity,
                                  @MappingTarget UserNotificationDto userNotificationDto) {
        var messageStatusDto = new EnumDto(notificationEntity.getMessageStatus().name(),
                notificationEntity.getMessageStatus().getDescription());
        userNotificationDto.setMessageStatus(messageStatusDto);
    }
}
