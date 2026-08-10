package com.ecaservice.notification.repository;

import com.ecaservice.notification.entity.NotificationOptionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link NotificationOptionsEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface NotificationOptionsRepository extends JpaRepository<NotificationOptionsEntity, Long> {

    /**
     * Finds user notification options.
     *
     * @param user - user login
     * @return user notification options entity
     */
    NotificationOptionsEntity findByUser(String user);
}
