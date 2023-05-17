package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.repository.AttributeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ecaservice.data.storage.util.Utils.getAttributeType;
import static com.ecaservice.data.storage.util.Utils.getAttributeValues;
import static eca.data.db.SqlQueryHelper.normalizeName;

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
     *
     * @param instancesEntity - instances entity
     * @param instances       - instances
     */
    public void saveAttributes(InstancesEntity instancesEntity, Instances instances) {
        log.info("Starting to save attributes info for instances [{}]", instancesEntity.getTableName());
        var attributesToSave = IntStream.range(0, instances.numAttributes())
                .mapToObj(i -> createAttributeEntity(instances.attribute(i), instancesEntity))
                .collect(Collectors.toList());
        attributeRepository.saveAll(attributesToSave);
        log.info("[{}] attributes info has been saved for instances [{}]", attributesToSave.size(),
                instancesEntity.getTableName());
    }

    private AttributeEntity createAttributeEntity(Attribute attribute, InstancesEntity instancesEntity) {
        var attributeEntity = new AttributeEntity();
        attributeEntity.setColumnName(normalizeName(attribute.name()));
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
