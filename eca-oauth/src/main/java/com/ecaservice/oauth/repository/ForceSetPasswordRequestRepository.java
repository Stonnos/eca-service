package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.ForceSetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository to manage with {@link ForceSetPasswordRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ForceSetPasswordRequestRepository extends JpaRepository<ForceSetPasswordRequestEntity, Long> {

    /**
     * Deletes active force set password requests.
     *
     * @param userEntity - user entity
     * @param date       - now date
     */
    @Modifying
    @Query("delete from ForceSetPasswordRequestEntity spr where spr.userEntity = :userEntity and " +
            "spr.expireDate > :date and spr.passwordDate is null")
    void deleteActiveRequests(@Param("userEntity") UserEntity userEntity,
                              @Param("date") LocalDateTime date);

    /**
     * Finds active set password request for specified token.
     *
     * @param token - token value
     * @param date  - search date
     * @return reset password request entity
     */
    Optional<ForceSetPasswordRequestEntity> findByTokenAndExpireDateAfterAndPasswordDateIsNull(String token,
                                                                                               LocalDateTime date);
}
