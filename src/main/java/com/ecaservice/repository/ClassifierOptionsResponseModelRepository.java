package com.ecaservice.repository;

import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ClassifierOptionsResponseModel} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ClassifierOptionsResponseModelRepository extends JpaRepository<ClassifierOptionsResponseModel, Long> {
}
