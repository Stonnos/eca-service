package com.ecaservice.repository;

import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.entity.ClassifiersConfigurationSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Implements repository that manages with {@link com.ecaservice.model.entity.ClassifiersConfiguration} entities.
 *
 * @author Roman Batygin
 */
public interface ClassifiersConfigurationRepository extends JpaRepository<ClassifiersConfiguration, Long> {

    /**
     * Gets classifiers configurations by specified source.
     *
     * @param source - configuration source
     * @param pageable - pageable object
     * @return classifiers configurations list
     */
    List<ClassifiersConfiguration> findAllBySource(ClassifiersConfigurationSource source, Pageable pageable);
}
