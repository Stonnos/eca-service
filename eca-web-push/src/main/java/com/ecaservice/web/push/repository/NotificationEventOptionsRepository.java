package com.ecaservice.web.push.repository;

import com.ecaservice.web.push.entity.NotificationEventOptionsEntity;
import com.ecaservice.web.push.entity.NotificationEventType;
import com.ecaservice.web.push.entity.NotificationOptionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link NotificationEventOptionsEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface NotificationEventOptionsRepository extends JpaRepository<NotificationEventOptionsEntity, Long> {

    /**
     * Gets user notification events with specified types.
     *
     * @param notificationOptionsEntity - notification options entity
     * @param eventTypes                - event types
     * @return user notification events list
     */
    List<NotificationEventOptionsEntity> findByNotificationOptionsAndEventTypeIn(
            NotificationOptionsEntity notificationOptionsEntity,
            Collection<NotificationEventType> eventTypes);
}
