package com.ecaservice.repository;

import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Implements repository that manages with {@link ClassifierOptionsDatabaseModel} entities.
 *
 * @author Roman Batygin
 */
public interface ClassifierOptionsDatabaseModelRepository extends JpaRepository<ClassifierOptionsDatabaseModel, Long> {

    /**
     * Finds all classifiers input options by specified configuration.
     *
     * @param configuration - classifiers configuration
     * @return classifier options database models list
     */
    List<ClassifierOptionsDatabaseModel> findAllByConfiguration(ClassifiersConfiguration configuration);
}
