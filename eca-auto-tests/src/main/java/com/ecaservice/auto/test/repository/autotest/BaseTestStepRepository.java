package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.BaseTestStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link BaseTestStepEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface BaseTestStepRepository extends JpaRepository<BaseTestStepEntity, Long> {

    /**
     * Checks test steps existing for specified evaluation request entity.
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @return {@code true} if test steps existing, otherwise {@code false}
     */
    boolean existsByEvaluationRequestEntity(BaseEvaluationRequestEntity evaluationRequestEntity);
}
