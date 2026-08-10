package com.ecaservice.web.push.mapping;

import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.UserNotificationDto;
import com.ecaservice.web.dto.model.push.PushRequestDto;
import com.ecaservice.web.push.dto.SystemPushRequest;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import com.ecaservice.web.push.entity.UserNotificationEntity;
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
    UserNotificationEntity map(UserPushNotificationRequest userPushNotificationRequest);

    /**
     * Maps user notification entity to dto model.
     *
     * @param userNotificationEntity - notification entity
     * @return user notification dto
     */
    @Mapping(target = "messageStatus", ignore = true)
    UserNotificationDto map(UserNotificationEntity userNotificationEntity);

    /**
     * Maps user notifications entities to dto list.
     *
     * @param notifications - user notifications entities
     * @return user notifications dto list
     */
    List<UserNotificationDto> map(List<UserNotificationEntity> notifications);

    /**
     * Maps system push request to push request dto,
     *
     * @param systemPushRequest - system push request
     * @return push request dto
     */
    @Mapping(target = "initiator", ignore = true)
    PushRequestDto map(SystemPushRequest systemPushRequest);

    /**
     * Maps user notification push request to push request dto,
     *
     * @param userPushNotificationRequest - user notification push request
     * @return push request dto
     */
    @Mapping(target = "showMessage", constant = "true")
    PushRequestDto mapUserPushRequest(UserPushNotificationRequest userPushNotificationRequest);

    /**
     * Maps notification parameters
     *
     * @param userPushNotificationRequest - user push notification request
     * @param userNotificationEntity          - notification entity
     */
    @AfterMapping
    default void mapParameters(UserPushNotificationRequest userPushNotificationRequest,
                               @MappingTarget UserNotificationEntity userNotificationEntity) {
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
            userNotificationEntity.setParameters(parameters);
        }
    }

    /**
     * Maps message status.
     *
     * @param userNotificationEntity  - notification entity
     * @param userNotificationDto - user notification dto
     */
    @AfterMapping
    default void mapMessageStatus(UserNotificationEntity userNotificationEntity,
                                  @MappingTarget UserNotificationDto userNotificationDto) {
        var messageStatusDto = new EnumDto(userNotificationEntity.getMessageStatus().name(),
                userNotificationEntity.getMessageStatus().getDescription());
        userNotificationDto.setMessageStatus(messageStatusDto);
    }
}
