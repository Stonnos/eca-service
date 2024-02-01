package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.UserNotificationEventOptionsEntity;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import com.ecaservice.user.profile.options.dto.UserNotificationEventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link UserNotificationEventOptionsEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface UserNotificationEventOptionsRepository
        extends JpaRepository<UserNotificationEventOptionsEntity, Long> {

    /**
     * Gets user notification events with specified types.
     *
     * @param userProfileOptionsEntity - user profile options entity
     * @param eventTypes               - event types
     * @return user notification events list
     */
    List<UserNotificationEventOptionsEntity> findByUserProfileOptionsAndEventTypeIn(
            UserProfileOptionsEntity userProfileOptionsEntity,
            Collection<UserNotificationEventType> eventTypes);
}
