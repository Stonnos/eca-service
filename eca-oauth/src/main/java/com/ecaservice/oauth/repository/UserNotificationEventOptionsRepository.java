package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.UserNotificationEventOptionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link UserNotificationEventOptionsEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface UserNotificationEventOptionsRepository extends JpaRepository<UserNotificationEventOptionsEntity, Long> {
}
