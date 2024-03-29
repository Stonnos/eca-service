package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.projections.ClassifiersOptionsStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
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
    List<ClassifierOptionsDatabaseModel> findAllByConfigurationOrderByCreationDateDesc(
            ClassifiersConfiguration configuration);

    /**
     * Finds all classifiers input options by specified configuration.
     *
     * @param configuration - classifiers configuration
     * @return classifier options database models list
     */
    List<ClassifierOptionsDatabaseModel> findAllByConfigurationOrderByCreationDate(
            ClassifiersConfiguration configuration);

    /**
     * Gets classifiers options statistics group by classifiers configuration.
     *
     * @param configurationsIds - configurations ids
     * @return classifiers options statistics as list
     */
    @Query("select c.configuration.id as configurationId, count(c.configuration.id) as classifiersOptionsCount " +
            "from ClassifierOptionsDatabaseModel c where c.configuration.id in (:configurationsIds) " +
            "group by c.configuration.id")
    List<ClassifiersOptionsStatistics> getClassifiersOptionsStatistics(
            @Param("configurationsIds") Collection<Long> configurationsIds);

    /**
     * Gets classifiers options page.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     * @param pageable                 - pageable object
     * @return classifiers options page
     */
    Page<ClassifierOptionsDatabaseModel> findAllByConfiguration(ClassifiersConfiguration classifiersConfiguration,
                                                                Pageable pageable);

    /**
     * Gets classifiers options count for specified configuration.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     * @return classifiers options count
     */
    long countByConfiguration(ClassifiersConfiguration classifiersConfiguration);
}
