package com.ecaservice.data.storage.repository;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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
}
