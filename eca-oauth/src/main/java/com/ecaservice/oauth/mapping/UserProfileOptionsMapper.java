package com.ecaservice.oauth.mapping;

import com.ecaservice.oauth.config.UserProfileProperties;
import com.ecaservice.oauth.entity.UserNotificationEventOptionsEntity;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.user.profile.options.dto.UserNotificationEventOptionsDto;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import com.ecaservice.web.dto.model.UserProfileNotificationEventOptionsDto;
import com.ecaservice.web.dto.model.UserProfileNotificationOptionsDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * User profile options mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface UserProfileOptionsMapper {

    /**
     * Maps user profile properties to entity model.
     *
     * @param userProfileProperties - user profile properties
     * @return user profile entity
     */
    UserProfileOptionsEntity map(UserProfileProperties userProfileProperties);

    /**
     * Maps user profile notification properties to entity model.
     *
     * @param userNotificationProperties - user profile notification properties
     * @return user notification options entity
     */
    UserNotificationEventOptionsEntity map(
            UserProfileProperties.UserNotificationEventProperties userNotificationProperties);

    /**
     * Maps user profile options entity to dto model.
     *
     * @param userProfileOptionsEntity - user profile options entity
     * @return user profile options dto
     */
    UserProfileOptionsDto mapToDto(UserProfileOptionsEntity userProfileOptionsEntity);

    /**
     * Maps user notification event options entity to dto model (for internal api).
     *
     * @param userNotificationEventOptionsEntity - user notification event options entity
     * @return user notification event options dto
     */
    UserNotificationEventOptionsDto mapToDto(UserNotificationEventOptionsEntity userNotificationEventOptionsEntity);

    /**
     * Maps user profile options entity to user profile notification options dto model.
     *
     * @param userProfileOptionsEntity - user profile options entity
     * @return user profile notification options
     */
    UserProfileNotificationOptionsDto mapToNotificationOptionsDto(UserProfileOptionsEntity userProfileOptionsEntity);

    /**
     * Maps user notification event options entity to user notification event options dto model (for web api).
     *
     * @param userNotificationEventOptionsEntity - user notification event options entity
     * @return user notification event options dto model
     */
    @Mapping(target = "eventDescription", ignore = true)
    UserProfileNotificationEventOptionsDto mapToNotificationEventOptionsDto(
            UserNotificationEventOptionsEntity userNotificationEventOptionsEntity);

    /**
     * Maps notification event description.
     *
     * @param userNotificationEventOptionsEntity     - user notification event options entity
     * @param userProfileNotificationEventOptionsDto - user notification event options dto model
     */
    @AfterMapping
    default void mapEventDescription(UserNotificationEventOptionsEntity userNotificationEventOptionsEntity,
                                     @MappingTarget
                                     UserProfileNotificationEventOptionsDto userProfileNotificationEventOptionsDto) {
        userProfileNotificationEventOptionsDto.setEventDescription(
                userNotificationEventOptionsEntity.getEventType().getDescription());
    }
}
