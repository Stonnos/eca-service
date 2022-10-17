package com.ecaservice.web.push.repository;

import com.ecaservice.web.push.entity.PushTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
