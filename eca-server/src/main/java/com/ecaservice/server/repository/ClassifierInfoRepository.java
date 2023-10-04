package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ClassifierInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Service to manage with {@link ClassifierInfo} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ClassifierInfoRepository extends JpaRepository<ClassifierInfo, Long> {
}
