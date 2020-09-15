package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository to manage with {@link UserEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface UserEntityRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    /**
     * Finds user by specified login or email address.
     *
     * @param userName - user name or email address
     * @return user entity
     */
    @Query("select u from UserEntity u where u.login = :userName or u.email = :userName")
    UserEntity findUser(@Param("userName") String userName);

    /**
     * Finds user by specified email.
     *
     * @param email - user email
     * @return user entity
     */
    Optional<UserEntity> findByEmail(String email);

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

    /**
     * Checks two factor authentication enabled for specified user.
     *
     * @param login - user login
     * @return {@code true} if user with specified login exists, otherwise {@code false}
     */
    @Query("select u.tfaEnabled from UserEntity u where u.login = :login")
    boolean isTfaEnabled(@Param("login") String login);
}
