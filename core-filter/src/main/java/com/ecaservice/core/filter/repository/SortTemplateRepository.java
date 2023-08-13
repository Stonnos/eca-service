package com.ecaservice.core.filter.repository;

import com.ecaservice.core.filter.entity.SortTemplate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Implements repository that manages with {@link SortTemplate} entities.
 *
 * @author Roman Batygin
 */
public interface SortTemplateRepository extends JpaRepository<SortTemplate, Long> {

    /**
     * Finds sort template by type.
     *
     * @param templateType - template type
     * @return sort template entity
     */
    @EntityGraph(value = "sortTemplates", type = EntityGraph.EntityGraphType.FETCH)
    Optional<SortTemplate> findByTemplateType(String templateType);
}
