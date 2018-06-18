package com.ecaservice.repository;

import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ClassifierOptionsRequestModel} entity.
 *
 * @author Roman Batygin
 */
public interface ClassifierOptionsRequestModelRepository extends JpaRepository<ClassifierOptionsRequestModel, Long> {
}
