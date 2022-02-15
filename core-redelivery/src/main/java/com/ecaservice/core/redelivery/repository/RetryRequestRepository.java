package com.ecaservice.core.redelivery.repository;

import com.ecaservice.core.redelivery.entity.RetryRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository to manage with {@link RetryRequest} persistence entity.
 *
 * @author Roman Batygin
 */
public interface RetryRequestRepository extends JpaRepository<RetryRequest, Long> {

    /**
     * Gets not sent retry requests ids.
     *
     * @return retry requests ids
     */
    @Query("select de.id from RetryRequest de order by de.createdAt")
    List<Long> getNotSentRequestIds();

    /**
     * Gets retry requests by ids.
     *
     * @param ids - retry requests ids
     * @return retry requests list
     */
    List<RetryRequest> findByIdIn(List<Long> ids);
}
