package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link BaseEvaluationRequestEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface BaseEvaluationRequestRepository extends JpaRepository<BaseEvaluationRequestEntity, Long> {
}
