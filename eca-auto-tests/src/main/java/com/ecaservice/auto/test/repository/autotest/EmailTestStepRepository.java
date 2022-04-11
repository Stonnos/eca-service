package com.ecaservice.auto.test.repository.autotest;

import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.EmailTestStepEntity;
import com.ecaservice.auto.test.model.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link EmailTestStepEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EmailTestStepRepository extends JpaRepository<EmailTestStepEntity, Long> {

    /**
     * Finds email step entity by request and type.
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @param emailType               - email type
     * @return email step entity
     */
    EmailTestStepEntity findByEvaluationRequestEntityAndEmailType(BaseEvaluationRequestEntity evaluationRequestEntity,
                                                                  EmailType emailType);
}
