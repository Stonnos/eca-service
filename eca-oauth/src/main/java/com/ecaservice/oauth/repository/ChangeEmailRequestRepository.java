package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.ChangeEmailRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
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
    @Query("select count(cr) > 0 from ChangeEmailRequestEntity cr where cr.userEntity = :userEntity and " +
            "cr.expireDate > :date and cr.confirmationDate is null " +
            "and cr.revocationExpireAt > :date and cr.revocationDate is null")
    boolean hasActiveChangeEmailRequest(@Param("userEntity") UserEntity userEntity,
                                        @Param("date") LocalDateTime date);

    /**
     * Finds active change email requests for specified user.
     *
     * @param token    - token value
     * @param date     - search date
     * @param pageable - pageable
     * @return change email request entity
     */
    @Query("select cr from ChangeEmailRequestEntity cr where cr.token = :token and " +
            "cr.expireDate > :date and cr.confirmationDate is null " +
            "and cr.revocationExpireAt > :date and cr.revocationDate is null")
    List<ChangeEmailRequestEntity> findActiveRequestsByToken(@Param("token") String token,
                                                             @Param("date") LocalDateTime date,
                                                             Pageable pageable);

    /**
     * Finds active change email requests for specified token.
     *
     * @param userEntity - user entity
     * @param date       - search date
     * @param pageable   - pageable
     * @return change email request entity
     */
    @Query("select cr from ChangeEmailRequestEntity cr where cr.userEntity = :userEntity and " +
            "cr.expireDate > :date and cr.confirmationDate is null " +
            "and cr.revocationExpireAt > :date and cr.revocationDate is null")
    List<ChangeEmailRequestEntity> findActiveRequestsByUser(@Param("userEntity") UserEntity userEntity,
                                                            @Param("date") LocalDateTime date,
                                                            Pageable pageable);

    /**
     * Finds requests to revoke.
     *
     * @param revocationToken - revocation token
     * @param date            - now date
     * @param pageable        - pageable
     * @return change email request entity
     */
    @Query("select cr from ChangeEmailRequestEntity cr where cr.revocationToken = :revocationToken and " +
            "cr.revocationExpireAt > :date and cr.revocationDate is null")
    List<ChangeEmailRequestEntity> findRequestsToRevoke(@Param("revocationToken") String revocationToken,
                                                        @Param("date") LocalDateTime date,
                                                        Pageable pageable);
}
