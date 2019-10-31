package com.ecaservice.repository;

import com.ecaservice.model.entity.FilterTemplate;
import com.ecaservice.model.entity.FilterTemplateType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Implements repository that manages with {@link FilterTemplate} entities.
 *
 * @author Roman Batygin
 */
public interface FilterTemplateRepository extends JpaRepository<FilterTemplate, Long> {

    /**
     * Finds filter template by type.
     *
     * @param templateType - template type
     * @return filter template entity
     */
    Optional<FilterTemplate> findFirstByTemplateType(FilterTemplateType templateType);
}
