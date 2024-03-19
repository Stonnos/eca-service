package com.ecaservice.user.profile.options.cache.client.repository;

import com.ecaservice.user.profile.options.cache.client.entity.UserProfileOptionsDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link UserProfileOptionsDataEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface UserProfileOptionsDataRepository extends JpaRepository<UserProfileOptionsDataEntity, Long> {

    /**
     * Finds user profile options data by specified user.
     *
     * @param user - user login
     * @return user profile options data entity
     */
    UserProfileOptionsDataEntity findByUser(String user);
}