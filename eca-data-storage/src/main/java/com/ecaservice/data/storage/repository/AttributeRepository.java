package com.ecaservice.data.storage.repository;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository to manage with {@link AttributeEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AttributeRepository extends JpaRepository<AttributeEntity, Long> {

    /**
     * Finds instances attributes ordered by index.
     *
     * @param instancesEntity - instances entity
     * @return attributes list
     */
    @EntityGraph(value = "attributeValues", type = EntityGraph.EntityGraphType.FETCH)
    List<AttributeEntity> findByInstancesEntityOrderByIndex(InstancesEntity instancesEntity);

    /**
     * Finds selected attributes for specified instances ordered by index.
     *
     * @param instancesEntity - instances entity
     * @return attributes list
     */
    @EntityGraph(value = "attributeValues", type = EntityGraph.EntityGraphType.FETCH)
    List<AttributeEntity> findByInstancesEntityAndSelectedIsTrueOrderByIndex(InstancesEntity instancesEntity);

    /**
     * Finds attribute by id.
     *
     * @param id - attribute id
     * @return attribute entity
     */
    @EntityGraph(value = "attributeValues", type = EntityGraph.EntityGraphType.FETCH)
    Optional<AttributeEntity> findById(Long id);

    /**
     * Selects all attributes for instances.
     *
     * @param instancesEntity - instances entity
     */
    @Modifying
    @Query("update AttributeEntity a set a.selected = true where a.instancesEntity = :instances")
    void selectAll(@Param("instances") InstancesEntity instancesEntity);
}
