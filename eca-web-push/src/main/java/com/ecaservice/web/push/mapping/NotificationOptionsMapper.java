package com.ecaservice.web.push.mapping;

import com.ecaservice.web.dto.model.UserProfileNotificationEventOptionsDto;
import com.ecaservice.web.dto.model.UserProfileNotificationOptionsDto;
import com.ecaservice.web.push.config.UserNotificationProperties;
import com.ecaservice.web.push.entity.NotificationEventOptionsEntity;
import com.ecaservice.web.push.entity.NotificationOptionsEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Notification options mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface NotificationOptionsMapper {

    /**
     * Maps user notifications properties to entity model.
     *
     * @param userNotificationProperties - user notifications properties
     * @return user notification options entity
     */
    @Mapping(target = "notificationEventOptions", ignore = true)
    NotificationOptionsEntity map(UserNotificationProperties userNotificationProperties);

    /**
     * Maps user notification properties to entity model.
     *
     * @param userNotificationProperties - user notification properties
     * @return user notification options entity
     */
    NotificationEventOptionsEntity map(
            UserNotificationProperties.UserNotificationEventProperties userNotificationProperties);

    /**
     * Maps user profile options entity to user profile notification options dto model.
     *
     * @param userProfileOptionsEntity - user profile options entity
     * @return user profile notification options
     */
    UserProfileNotificationOptionsDto mapToNotificationOptionsDto(NotificationOptionsEntity userProfileOptionsEntity);

    /**
     * Maps user notification event options entity to user notification event options dto model (for web api).
     *
     * @param notificationEventOptionsEntity - user notification event options entity
     * @return user notification event options dto model
     */
    @Mapping(target = "eventDescription", ignore = true)
    UserProfileNotificationEventOptionsDto mapToNotificationEventOptionsDto(
            NotificationEventOptionsEntity notificationEventOptionsEntity);

    /**
     * Maps notification event description.
     *
     * @param notificationEventOptionsEntity     - user notification event options entity
     * @param eventOptionsDto - user notification event options dto model
     */
    @AfterMapping
    default void mapEventDescription(NotificationEventOptionsEntity notificationEventOptionsEntity,
                                     @MappingTarget UserProfileNotificationEventOptionsDto eventOptionsDto) {
        eventOptionsDto.setEventDescription(
                notificationEventOptionsEntity.getEventType().getDescription());
    }
}
