package com.ecaservice.data.storage.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.data.storage.config.CacheNames;
import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.model.AttributeInfo;
import com.ecaservice.data.storage.repository.AttributeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ecaservice.data.storage.util.Utils.getAttributeType;
import static com.ecaservice.data.storage.util.Utils.getAttributeValues;
import static eca.data.db.SqlQueryHelper.formatName;

/**
 * Attributes service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttributeService {

    private final AttributeRepository attributeRepository;

    /**
     * Saves instances attributes info into database.
     * Each attribute is formatted according to the following rules:
     * 1. Each special character is transformed to '_' symbol
     * 2. Each attribute name is truncated to 255 length, if its length greater than 255 symbols
     * 3. Each attribute name is casted to lower case
     *
     * @param instancesEntity - instances entity
     * @param instances       - instances
     * @return saved attributes list
     */
    public List<AttributeEntity> saveAttributes(InstancesEntity instancesEntity, Instances instances) {
        log.info("Starting to save attributes info for instances [{}]", instancesEntity.getTableName());
        var attributesToSave = IntStream.range(0, instances.numAttributes())
                .mapToObj(i -> createAttributeEntity(instances.attribute(i), instancesEntity))
                .collect(Collectors.toList());
        attributeRepository.saveAll(attributesToSave);
        log.info("[{}] attributes info has been saved for instances [{}]", attributesToSave.size(),
                instancesEntity.getTableName());
        return attributesToSave;
    }

    /**
     * Gets attributes list for specified instances.
     *
     * @param instancesEntity - instances entity
     * @return attributes list
     */
    public List<AttributeEntity> getAttributes(InstancesEntity instancesEntity) {
        log.debug("Gets attributes list for instances [{}]", instancesEntity.getTableName());
        return attributeRepository.findByInstancesEntityOrderByIndex(instancesEntity);
    }

    /**
     * Gets attributes info list for specified instances.
     *
     * @param instancesEntity - instances entity
     * @return attributes list
     */
    @Cacheable(CacheNames.ATTRIBUTES_CACHE)
    public List<AttributeInfo> getsAttributesInfo(InstancesEntity instancesEntity) {
        log.info("Gets attributes info list for instances [{}]", instancesEntity.getTableName());
        var attributes = attributeRepository.getsAttributesInfo(instancesEntity);
        log.info("[{}] attributes info has been fetched for instances [{}]", attributes.size(),
                instancesEntity.getTableName());
        return attributes.stream()
                .map(attributeInfoProjection -> new AttributeInfo(attributeInfoProjection.getColumnName(),
                        attributeInfoProjection.getType()))
                .collect(Collectors.toList());
    }

    /**
     * Deletes instances attributes.
     *
     * @param instancesEntity - instances entity
     */
    public void deleteAttributes(InstancesEntity instancesEntity) {
        log.info("Starting to delete instances [{}] attributes", instancesEntity.getTableName());
        var attributes = attributeRepository.findByInstancesEntityOrderByIndex(instancesEntity);
        attributeRepository.deleteAll(attributes);
        log.info("[{}] attributes has been deleted for instances [{}]", attributes.size(),
                instancesEntity.getTableName());
    }

    /**
     * Gets attribute with specified id.
     *
     * @param id - attribute id
     * @return attribute entity
     */
    public AttributeEntity getById(long id) {
        log.debug("Gets attribute with id [{}]", id);
        return attributeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(AttributeEntity.class, id));
    }

    private AttributeEntity createAttributeEntity(Attribute attribute, InstancesEntity instancesEntity) {
        var attributeEntity = new AttributeEntity();
        attributeEntity.setColumnName(formatName(attribute.name()));
        attributeEntity.setIndex(attribute.index());
        attributeEntity.setType(getAttributeType(attribute));
        if (attribute.isNominal()) {
            attributeEntity.setValues(getAttributeValues(attribute));
        }
        attributeEntity.setSelected(true);
        attributeEntity.setInstancesEntity(instancesEntity);
        return attributeEntity;
    }
}
