package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository to manage with {@link UserEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface UserEntityRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    /**
     * Finds user by specified login.
     *
     * @param login - user login
     * @return user entity
     */
    UserEntity findByLogin(String login);

    /**
     * Checks user existing with specified login.
     *
     * @param login - user login
     * @return {@code true} if user with specified login exists, otherwise {@code false}
     */
    boolean existsByLogin(String login);

    /**
     * Checks user existing with specified email.
     *
     * @param email - user email
     * @return {@code true} if user with specified email exists, otherwise {@code false}
     */
    boolean existsByEmail(String email);
}
