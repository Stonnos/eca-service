package com.ecaservice.web.push.repository;

import com.ecaservice.web.push.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

/**
 * Repository to manage with {@link NotificationEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    /**
     * Finds user notifications page.
     *
     * @param receiver - receiver user
     * @param dateTime - date time bound for search
     * @param pageable - pageable
     * @return user notifications page
     */
    Page<NotificationEntity> findByReceiverAndCreatedIsAfter(String receiver, LocalDateTime dateTime,
                                                             Pageable pageable);
}
