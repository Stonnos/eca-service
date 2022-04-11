package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ErsRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository to manage with {@link ErsRequest} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ErsRequestRepository extends JpaRepository<ErsRequest, Long> {

    /**
     * Finds ers request by request id.
     *
     * @param requestId - request id
     * @return ers request
     */
    Optional<ErsRequest> findByRequestId(String requestId);
}
