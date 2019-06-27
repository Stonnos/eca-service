package com.ecaservice.repository;

import com.ecaservice.model.entity.FilterDictionary;
import com.ecaservice.model.entity.FilterTemplate;
import com.ecaservice.model.entity.FilterTemplateType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Implements repository that manages with {@link FilterDictionary} entities.
 *
 * @author Roman Batygin
 */
public interface FilterDictionaryRepository extends JpaRepository<FilterDictionary, Long> {

    /**
     * Gets filter dictionary by name.
     *
     * @param name - dictionary name
     * @return filter dictionary entity
     */
    FilterDictionary findByName(String name);
}
