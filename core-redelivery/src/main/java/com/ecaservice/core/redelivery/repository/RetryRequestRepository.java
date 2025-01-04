package com.ecaservice.core.redelivery.repository;

import com.ecaservice.core.redelivery.entity.RetryRequest;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.hibernate.cfg.AvailableSettings.JAKARTA_LOCK_TIMEOUT;

/**
 * Repository to manage with {@link RetryRequest} persistence entity.
 *
 * @author Roman Batygin
 */
public interface RetryRequestRepository extends JpaRepository<RetryRequest, Long> {

    /**
     * Indicates that rows which are already locked should be skipped.
     */
    String SKIP_LOCKED = "-2";

    /**
     * Gets not sent retry requests.
     *
     * @param date     - date bound
     * @param pageable - pageable object
     * @return retry requests page
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = JAKARTA_LOCK_TIMEOUT, value = SKIP_LOCKED)})
    @Query("select re from RetryRequest re where (re.lockedTtl is null or re.lockedTtl < :date) and " +
            "(re.retryAt is null or re.retryAt < :date) order by re.createdAt")
    List<RetryRequest> getNotSentRequests(@Param("date") LocalDateTime date, Pageable pageable);

    /**
     * Locks specified retry requests.
     *
     * @param ids     - retry requests ids
     * @param nowTime - now time
     */
    @Modifying
    @Query("update RetryRequest re set re.lockedTtl = :nowTime where re.id in (:ids)")
    void lock(@Param("ids") Collection<Long> ids, @Param("nowTime") LocalDateTime nowTime);
}
