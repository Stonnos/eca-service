package com.ecaservice.repository;

import com.ecaservice.model.entity.FilterDictionary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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
    Optional<FilterDictionary> findByName(String name);
}
