package com.ecaservice.repository;

import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository to manage with {@link ClassifierOptionsRequestModel} entity.
 *
 * @author Roman Batygin
 */
public interface ClassifierOptionsRequestModelRepository extends JpaRepository<ClassifierOptionsRequestModel, Long>,
        JpaSpecificationExecutor<ClassifierOptionsRequestModel> {
}
