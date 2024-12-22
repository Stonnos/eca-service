package com.ecaservice.core.redelivery.repository;

import com.ecaservice.core.redelivery.entity.RetryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * Repository to manage with {@link RetryRequest} persistence entity.
 *
 * @author Roman Batygin
 */
public interface RetryRequestRepository extends JpaRepository<RetryRequest, Long> {

    /**
     * Gets not sent retry requests.
     *
     * @param date     - date bound
     * @param pageable - pageable object
     * @return retry requests page
     */
    @Query("select re from RetryRequest re where re.retryAt is null or re.retryAt < :date order by re.createdAt")
    Page<RetryRequest> getNotSentRequests(@Param("date") LocalDateTime date, Pageable pageable);
}
