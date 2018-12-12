package com.ecaservice.repository;

import com.ecaservice.model.entity.FilterTemplate;
import com.ecaservice.web.dto.model.FilterTemplateType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Implements repository that manages with {@link FilterTemplate} entities.
 *
 * @author Roman Batygin
 */
public interface FilterTemplateRepository extends JpaRepository<FilterTemplate, Long> {

    /**
     * Finds filter template be type.
     *
     * @param templateType - template type
     * @return filter template entity
     */
    //@EntityGraph(value = "filterFields", type = EntityGraph.EntityGraphType.FETCH)
    FilterTemplate findFirstByTemplateType(FilterTemplateType templateType);
}
