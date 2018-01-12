package com.ecaservice.repository;

import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Implements repository that manages with {@link ClassifierOptionsDatabaseModel} entities.
 *
 * @author Roman Batygin
 */
public interface ClassifierOptionsDatabaseModelRepository extends JpaRepository<ClassifierOptionsDatabaseModel, Long> {

    /**
     * Finds the latest classifiers input options version.
     *
     * @return the latest classifiers input options version
     */
    @Query("select max(c.version) from ClassifierOptionsDatabaseModel c")
    Integer findLatestVersion();

    /**
     * Finds all classifiers input options by specified version.
     *
     * @param version input options version
     * @return {@link ClassifierOptionsDatabaseModel} list
     */
    List<ClassifierOptionsDatabaseModel> findAllByVersion(int version);
}
