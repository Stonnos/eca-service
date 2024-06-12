package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.TfaCodeEntity;
import com.ecaservice.oauth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * Repository to manage with {@link TfaCodeEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface TfaCodeRepository extends JpaRepository<TfaCodeEntity, Long> {

    /**
     * Deletes active tfa codes for specified user.
     *
     * @param userEntity - user entity
     * @param date       - now date
     */
    @Modifying
    @Query("delete from TfaCodeEntity tc where tc.userEntity = :userEntity and tc.expireDate > :date")
    void deleteActiveCodes(@Param("userEntity") UserEntity userEntity,
                           @Param("date") LocalDateTime date);

    /**
     * Finds tfa code entity.
     *
     * @param token - code value
     * @return tfa code entity
     */
    TfaCodeEntity findByToken(String token);
}
