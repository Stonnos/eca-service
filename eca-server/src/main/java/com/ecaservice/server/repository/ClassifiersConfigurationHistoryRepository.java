package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Implements repository to manage with {@link ClassifiersConfigurationHistoryEntity} entity.
 *
 * @author Roman Batygin
 */
public interface ClassifiersConfigurationHistoryRepository
        extends JpaRepository<ClassifiersConfigurationHistoryEntity, Long>,
        JpaSpecificationExecutor<ClassifiersConfigurationHistoryEntity> {

    /**
     * Deletes all classifiers configuration history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     * @return deleted rows count
     */
    long deleteAllByConfiguration(ClassifiersConfiguration classifiersConfiguration);

    /**
     * Gets classifiers configuration another modifiers.
     *
     * @param configuration - classifier configuration entity
     * @param user          - current user
     * @return modifiers list
     */
    @Query("select distinct ch.createdBy from ClassifiersConfigurationHistoryEntity ch " +
            "where ch.configuration = :configuration and ch.createdBy <> :user")
    List<String> getAnotherModifiers(@Param("configuration") ClassifiersConfiguration configuration,
                                     @Param("user") String user);

    /**
     * Gets classifiers configuration modifications count by another users.
     *
     * @param configuration - classifier configuration entity
     * @param user          - current user
     * @return modifications count
     */
    @Query("select count(ch) from ClassifiersConfigurationHistoryEntity ch " +
            "where ch.configuration = :configuration and ch.createdBy <> :user")
    long getAnotherUsersModificationsCount(@Param("configuration") ClassifiersConfiguration configuration,
                                           @Param("user") String user);
}
