package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository to manage with {@link ResetPasswordRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ResetPasswordRequestRepository extends JpaRepository<ResetPasswordRequestEntity, Long> {

    /**
     * Finds active reset password request for user.
     *
     * @param userEntity - user entity
     * @param date       - search date
     * @return reset password request entity
     */
    ResetPasswordRequestEntity findByUserEntityAndExpireDateAfterAndResetDateIsNull(UserEntity userEntity,
                                                                                    LocalDateTime date);

    /**
     * Finds active reset password request for specified token
     *
     * @param token - token value
     * @param date  - search date
     * @return reset password request entity
     */
    Optional<ResetPasswordRequestEntity> findByTokenAndExpireDateAfterAndResetDateIsNull(String token,
                                                                                         LocalDateTime date);
}
