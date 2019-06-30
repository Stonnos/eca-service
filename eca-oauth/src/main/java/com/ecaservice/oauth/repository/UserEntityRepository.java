package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link UserEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Finds user by specified login.
     *
     * @param login - user login
     * @return user entity
     */
    UserEntity findByLogin(String login);
}
