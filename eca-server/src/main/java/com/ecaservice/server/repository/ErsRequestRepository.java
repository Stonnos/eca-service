package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ErsRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ErsRequest} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ErsRequestRepository extends JpaRepository<ErsRequest, Long> {
}
