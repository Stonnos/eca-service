package com.ecaservice.repository;

import com.ecaservice.model.entity.ClassifiersConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Implements repository that manages with {@link com.ecaservice.model.entity.ClassifiersConfiguration} entities.
 *
 * @author Roman Batygin
 */
public interface ClassifiersConfigurationRepository extends JpaRepository<ClassifiersConfiguration, Long> {

    /**
     * Gets classifiers configurations by specified source.
     *
     * @return classifiers configurations list
     */
    ClassifiersConfiguration findFirstByBuildInIsTrue();

    /**
     * Gets active classifiers configuration.
     *
     * @return classifiers configuration entity
     */
    Optional<ClassifiersConfiguration> findFirstByActiveTrue();
}
