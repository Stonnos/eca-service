package com.ecaservice.repository;

import com.ecaservice.model.entity.ErsRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ErsRequest} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ErsRequestRepository extends JpaRepository<ErsRequest, Long> {
}
