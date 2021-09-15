package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.ChangeEmailRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository to manage with {@link ChangeEmailRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ChangeEmailRequestRepository extends JpaRepository<ChangeEmailRequestEntity, Long> {

    /**
     * Finds active change email request for user.
     *
     * @param userEntity - user entity
     * @param date       - search date
     * @return {@code true} if active change email request exists
     */
    boolean existsByUserEntityAndExpireDateAfterAndConfirmationDateIsNull(UserEntity userEntity,
                                                                          LocalDateTime date);

    /**
     * Finds active change email request for specified token.
     *
     * @param token - token value
     * @param date  - search date
     * @return change email request entity
     */
    Optional<ChangeEmailRequestEntity> findByTokenAndExpireDateAfterAndConfirmationDateIsNull(String token,
                                                                                              LocalDateTime date);
}
