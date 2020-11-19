package com.ecaservice.external.api.test.repository;

import com.ecaservice.external.api.test.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link JobEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface JobRepository extends JpaRepository<JobEntity, Long> {
}
