package com.ecaservice.data.storage.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.common.web.exception.InvalidOperationException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.data.storage.config.CacheNames;
import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.AttributeValueEntity;
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

import static com.ecaservice.data.storage.config.audit.AuditCodes.SELECT_ATTRIBUTE;
import static com.ecaservice.data.storage.config.audit.AuditCodes.UNSELECT_ATTRIBUTE;
import static com.ecaservice.data.storage.util.Utils.getAttributeType;
import static com.ecaservice.data.storage.util.Utils.getAttributeValues;

/**
 * Attributes service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttributeService {

    private static final String COLUMN_NAME_FORMAT = "column_%d";

    private final AttributeRepository attributeRepository;

    /**
     * Saves instances attributes info into database.
     *
     * @param instancesEntity - instances entity
     * @param instances       - instances
     * @param attributeNames  - attribute names list
     * @return saved attributes list
     */
    public List<AttributeEntity> saveAttributes(InstancesEntity instancesEntity,
                                                Instances instances,
                                                List<String> attributeNames) {
        log.info("Starting to save attributes info for instances [{}]", instancesEntity.getRelationName());
        var attributesToSave = IntStream.range(0, instances.numAttributes())
                .mapToObj(i -> createAttributeEntity(attributeNames.get(i), instances.attribute(i), instancesEntity))
                .collect(Collectors.toList());
        attributeRepository.saveAll(attributesToSave);
        log.info("[{}] attributes info has been saved for instances [{}]", attributesToSave.size(),
                instancesEntity.getRelationName());
        return attributesToSave;
    }

    /**
     * Gets attributes list for specified instances.
     *
     * @param instancesEntity - instances entity
     * @return attributes list
     */
    public List<AttributeEntity> getAttributes(InstancesEntity instancesEntity) {
        log.debug("Gets attributes list for instances [{}]", instancesEntity.getRelationName());
        return attributeRepository.findByInstancesEntityOrderByIndex(instancesEntity);
    }

    /**
     * Gets selected attributes list for specified instances.
     *
     * @param instancesEntity - instances entity
     * @return attributes list
     */
    public List<AttributeEntity> getSelectedAttributes(InstancesEntity instancesEntity) {
        log.debug("Gets selected attributes list for instances [{}]", instancesEntity.getRelationName());
        return attributeRepository.findByInstancesEntityAndSelectedIsTrueOrderByIndex(instancesEntity);
    }

    /**
     * Gets attributes info list for specified instances.
     *
     * @param instancesEntity - instances entity
     * @return attributes list
     */
    @Cacheable(value = CacheNames.ATTRIBUTES_CACHE, key = "#instancesEntity.id")
    public List<AttributeInfo> getsAttributesInfo(InstancesEntity instancesEntity) {
        log.info("Gets attributes info list for instances [{}]", instancesEntity.getRelationName());
        var attributes = attributeRepository.findByInstancesEntityOrderByIndex(instancesEntity);
        log.info("[{}] attributes info has been fetched for instances [{}]", attributes.size(),
                instancesEntity.getTableName());
        return attributes.stream()
                .map(attributeEntity -> {
                    var attributeInfo = new AttributeInfo();
                    attributeInfo.setColumnName(attributeEntity.getColumnName());
                    attributeInfo.setAttributeName(attributeEntity.getAttributeName());
                    attributeInfo.setType(attributeEntity.getType());
                    if (AttributeType.NOMINAL.equals(attributeEntity.getType())) {
                        var values = attributeEntity.getValues()
                                .stream()
                                .map(AttributeValueEntity::getValue)
                                .collect(Collectors.toList());
                        attributeInfo.setValues(values);
                    }
                    return attributeInfo;
                })
                .collect(Collectors.toList());
    }

    /**
     * Deletes instances attributes.
     *
     * @param instancesEntity - instances entity
     */
    public void deleteAttributes(InstancesEntity instancesEntity) {
        log.info("Starting to delete instances [{}] attributes", instancesEntity.getRelationName());
        var attributes = attributeRepository.findByInstancesEntityOrderByIndex(instancesEntity);
        attributeRepository.deleteAll(attributes);
        log.info("[{}] attributes has been deleted for instances [{}]", attributes.size(),
                instancesEntity.getRelationName());
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

    /**
     * Selects attribute for classification.
     *
     * @param id - attribute id
     */
    @Audit(value = SELECT_ATTRIBUTE, correlationIdKey = "#result.instancesEntity.id")
    public AttributeEntity selectAttribute(long id) {
        log.info("Starting to select attribute [{}]", id);
        var attribute = getById(id);
        if (attribute.isSelected()) {
            throw new InvalidOperationException(String.format("Attribute [%d] is already selected", id));
        }
        attribute.setSelected(true);
        attributeRepository.save(attribute);
        log.info("Attribute [{}] has been selected", attribute.getId());
        return attribute;
    }

    /**
     * Unselects attribute for classification.
     *
     * @param id - attribute id
     */
    @Audit(value = UNSELECT_ATTRIBUTE, correlationIdKey = "#result.instancesEntity.id")
    public AttributeEntity unselectAttribute(long id) {
        log.info("Starting to unselect attribute [{}]", id);
        var attribute = getById(id);
        if (!attribute.isSelected()) {
            throw new InvalidOperationException(String.format("Attribute [%d] is already unselected", id));
        }
        attribute.setSelected(false);
        attributeRepository.save(attribute);
        log.info("Attribute [{}] has been unselected", attribute.getId());
        return attribute;
    }

    private AttributeEntity createAttributeEntity(String attributeName,
                                                  Attribute attribute,
                                                  InstancesEntity instancesEntity) {
        var attributeEntity = new AttributeEntity();
        attributeEntity.setAttributeName(attributeName);
        attributeEntity.setColumnName(attribute.name());
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
