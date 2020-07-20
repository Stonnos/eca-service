package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.DataStorageException;
import com.ecaservice.data.storage.repository.InstancesRepository;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.DataResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import weka.core.Instances;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Service for saving data file into database.
 *
 * @author Roman Batygin
 */
@Validated
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final FileDataLoader fileDataLoader;
    private final InstancesService instancesService;
    private final InstancesRepository instancesRepository;

    /**
     * Saves training data file into database.
     *
     * @param dataResource - data source
     * @param tableName    - table name
     */
    public <S> InstancesEntity saveData(@NotNull DataResource<S> dataResource, @NotBlank String tableName) {
        fileDataLoader.setSource(dataResource);
        log.info("Starting to save file '{}'.", dataResource.getFile());
        try {
            Instances instances = fileDataLoader.loadInstances();
            log.info("Data has been loaded from file '{}'", dataResource.getFile());
            instancesService.saveInstances(tableName, instances);
            return saveInstancesEntity(tableName, instances);
        } catch (Exception ex) {
            throw new DataStorageException(ex.getMessage());
        }
    }

    private InstancesEntity saveInstancesEntity(String tableName, Instances instances) {
        InstancesEntity instancesEntity = new InstancesEntity();
        instancesEntity.setTableName(tableName);
        instancesEntity.setNumAttributes(instances.numAttributes());
        instancesEntity.setNumInstances(instances.numInstances());
        instancesEntity.setCreated(LocalDateTime.now());
        return instancesRepository.save(instancesEntity);
    }
}
