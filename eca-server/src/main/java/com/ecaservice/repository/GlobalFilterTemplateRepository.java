package com.ecaservice.repository;

import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.model.entity.GlobalFilterTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link GlobalFilterTemplate} entities.
 *
 * @author Roman Batygin
 */
public interface GlobalFilterTemplateRepository extends JpaRepository<GlobalFilterTemplate, Long> {

    /**
     * Finds global filter template by type.
     *
     * @param templateType - template type
     * @return global filter template entity
     */
    GlobalFilterTemplate findFirstByTemplateType(FilterTemplateType templateType);
}
