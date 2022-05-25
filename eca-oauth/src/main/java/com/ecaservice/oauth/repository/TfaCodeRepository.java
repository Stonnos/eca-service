package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.TfaCodeEntity;
import com.ecaservice.oauth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository to manage with {@link TfaCodeEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface TfaCodeRepository extends JpaRepository<TfaCodeEntity, Long> {

    /**
     * Finds active tfa codes.
     *
     * @param userEntity - user entity
     * @param date       - date bound
     * @return tfa codes list
     */
    @Query("select tc from TfaCodeEntity tc where tc.userEntity = :userEntity and tc.expireDate > :date")
    List<TfaCodeEntity> findActiveCodes(@Param("userEntity") UserEntity userEntity,
                                        @Param("date") LocalDateTime date);

    /**
     * Finds tfa code entity.
     *
     * @param token    - code value
     * @param dateTime - now date time value
     * @return tfa code entity
     */
    TfaCodeEntity findByTokenAndExpireDateAfter(String token, LocalDateTime dateTime);

    /**
     * Gets expired codes ids.
     *
     * @param date - date bound
     * @return expired codes ids
     */
    @Query("select tc from TfaCodeEntity tc where tc.expireDate < :date")
    List<Long> getExpiredCodeIds(@Param("date") LocalDateTime date);

    /**
     * Gets tfa codes by ids.
     *
     * @param ids - tfa codes ids
     * @return tfa codes list
     */
    List<TfaCodeEntity> findByIdIn(List<Long> ids);
}
