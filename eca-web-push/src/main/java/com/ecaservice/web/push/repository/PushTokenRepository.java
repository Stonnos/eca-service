package com.ecaservice.web.push.repository;

import com.ecaservice.web.push.entity.PushTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository to manage with {@link PushTokenEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface PushTokenRepository extends JpaRepository<PushTokenEntity, Long> {

    /**
     * Gets push token entity for specified user.
     *
     * @param user - user name
     * @return push token entity
     */
    PushTokenEntity findByUser(String user);

    /**
     * Gets not expired users push tokens.
     *
     * @param users    - users list
     * @param dateTime - date time bound
     * @return push tokens list
     */
    @Query("select pt from PushTokenEntity pt where pt.user in (:users) and pt.expireAt > :dateTime")
    List<PushTokenEntity> getNotExpiredTokens(@Param("users") List<String> users,
                                              @Param("dateTime") LocalDateTime dateTime);
}
