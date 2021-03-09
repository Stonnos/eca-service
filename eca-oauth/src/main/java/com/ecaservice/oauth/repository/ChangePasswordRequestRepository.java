package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository to manage with {@link ChangePasswordRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ChangePasswordRequestRepository extends JpaRepository<ChangePasswordRequestEntity, Long> {

    /**
     * Finds active change password request for user.
     *
     * @param userEntity - user entity
     * @param date       - search date
     * @return change password request entity
     */
    ChangePasswordRequestEntity findByUserEntityAndExpireDateAfterAndConfirmationDateIsNull(UserEntity userEntity,
                                                                                            LocalDateTime date);

    /**
     * Finds active change password request for specified token.
     *
     * @param token - token value
     * @param date  - search date
     * @return change password request entity
     */
    Optional<ChangePasswordRequestEntity> findByTokenAndExpireDateAfterAndConfirmationDateIsNull(String token,
                                                                                                 LocalDateTime date);
}
