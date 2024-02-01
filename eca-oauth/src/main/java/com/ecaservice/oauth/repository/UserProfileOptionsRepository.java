package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.entity.UserProfileOptionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository to manage with {@link UserProfileOptionsEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface UserProfileOptionsRepository extends JpaRepository<UserProfileOptionsEntity, Long> {

    /**
     * Finds user profile options.
     *
     * @param userEntity - user entity
     * @return user profile options entity
     */
    UserProfileOptionsEntity findByUserEntity(UserEntity userEntity);
}
