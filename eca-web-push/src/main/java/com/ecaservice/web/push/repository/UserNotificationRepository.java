package com.ecaservice.web.push.repository;

import com.ecaservice.web.push.entity.UserNotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link UserNotificationEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface UserNotificationRepository extends JpaRepository<UserNotificationEntity, Long> {

    /**
     * Finds user notifications page.
     *
     * @param receiver - receiver user
     * @param dateTime - date time bound for search
     * @param pageable - pageable
     * @return user notifications page
     */
    Page<UserNotificationEntity> findByReceiverAndCreatedIsAfterOrderByCreatedDesc(String receiver,
                                                                                   LocalDateTime dateTime,
                                                                                   Pageable pageable);

    /**
     * Gets not read notifications count.
     *
     * @param receiver - receiver user
     * @param dateTime - date time bound for search
     * @return not read notifications count
     */
    @Query("select count(n) from UserNotificationEntity n where n.receiver = :receiver " +
            "and n.created > :dateTime " +
            "and n.messageStatus = 'NOT_READ'"
    )
    long getNotReadNotificationsCount(@Param("receiver") String receiver,
                                      @Param("dateTime") LocalDateTime dateTime);

    /**
     * Reads all notifications from specified date.
     *
     * @param receiver - receiver
     * @param dateTime - date time
     * @return read notifications count
     */
    @Modifying
    @Query("update UserNotificationEntity n set n.messageStatus = 'READ' " +
            "where n.receiver = :receiver and n.created > :dateTime and n.messageStatus = 'NOT_READ'")
    int readAllNotifications(@Param("receiver") String receiver,
                              @Param("dateTime") LocalDateTime dateTime);

    /**
     * Reads specified notifications.
     *
     * @param receiver - receiver user
     * @param ids      - notifications ids
     * @return read notifications count
     */
    @Modifying
    @Query("update UserNotificationEntity n set n.messageStatus = 'READ' " +
            "where n.receiver = :receiver and n.id in (:ids)")
    int readNotifications(@Param("receiver") String receiver,
                           @Param("ids") Collection<Long> ids);

    /**
     * Gets notification ids contains in specified ids list
     *
     * @param ids      - notification ids list
     * @param receiver - receiver user
     * @return notification ids contains in db
     */
    @Query("select n.id from UserNotificationEntity n where n.id in (:ids) and n.receiver = :receiver")
    List<Long> getNotifications(@Param("ids") Collection<Long> ids, @Param("receiver") String receiver);
}
