package com.ecaservice.web.push.repository;

import com.ecaservice.web.push.entity.NotificationOptionsEntity;
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
