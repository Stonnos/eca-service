package com.ecaservice.web.push.repository;

import com.ecaservice.web.push.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link NotificationEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}