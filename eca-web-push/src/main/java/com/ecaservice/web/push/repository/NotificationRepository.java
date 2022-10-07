package com.ecaservice.web.push.repository;

import com.ecaservice.web.push.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    Page<NotificationEntity> findByReceiverAndCreatedIsAfterOrderByCreatedDesc(String receiver, LocalDateTime dateTime,
                                                                               Pageable pageable);

    /**
     * Gets not read notifications count.
     *
     * @param receiver - receiver user
     * @param dateTime - date time bound for search
     * @return not read notifications count
     */
    @Query("select count(n) from NotificationEntity n where n.receiver = :receiver " +
            "and n.created > :dateTime " +
            "and n.messageStatus = 'NOT_READ'"
    )
    long getNotReadNotificationsCount(@Param("receiver") String receiver,
                                      @Param("dateTime") LocalDateTime dateTime);
}
