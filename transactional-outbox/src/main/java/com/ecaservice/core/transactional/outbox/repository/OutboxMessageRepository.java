package com.ecaservice.core.transactional.outbox.repository;

import com.ecaservice.core.transactional.outbox.entity.OutboxMessageEntity;
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
 * Repository to manage with {@link OutboxMessageEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface OutboxMessageRepository extends JpaRepository<OutboxMessageEntity, Long> {

    /**
     * Indicates that rows which are already locked should be skipped.
     */
    String SKIP_LOCKED = "-2";

    /**
     * Gets not sent outbox messages.
     *
     * @param date     - date bound
     * @param pageable - pageable object
     * @return retry requests page
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = JAKARTA_LOCK_TIMEOUT, value = SKIP_LOCKED)})
    @Query("select om from OutboxMessageEntity om where (om.lockedTtl is null or om.lockedTtl < :date) order by om.createdAt")
    List<OutboxMessageEntity> getNotSentMessages(@Param("date") LocalDateTime date, Pageable pageable);

    /**
     * Locks specified messages.
     *
     * @param ids     - messages ids
     * @param nowTime - now time
     */
    @Modifying
    @Query("update OutboxMessageEntity om set om.lockedTtl = :nowTime where om.id in (:ids)")
    void lock(@Param("ids") Collection<Long> ids, @Param("nowTime") LocalDateTime nowTime);
}
