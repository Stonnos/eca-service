package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.DataStorageException;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.DataResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.core.Instances;

import java.time.LocalDateTime;

import static com.ecaservice.data.storage.entity.InstancesEntity_.CREATED;
import static com.ecaservice.data.storage.util.FilterUtils.buildSort;
import static com.ecaservice.data.storage.util.FilterUtils.buildSpecification;

/**
 * Service for saving data file into database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final EcaDsConfig ecaDsConfig;
    private final FileDataLoader fileDataLoader;
    private final InstancesService instancesService;
    private final InstancesRepository instancesRepository;

    /**
     * Gets the next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @return entities page
     */
    public Page<InstancesEntity> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATED, pageRequestDto.isAscending());
        Specification<InstancesEntity> specification = buildSpecification(pageRequestDto);
        int pageSize = Integer.min(pageRequestDto.getSize(), ecaDsConfig.getMaxPageSize());
        return instancesRepository.findAll(specification, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }

    /**
     * Saves training data file into database.
     *
     * @param dataResource - data source
     * @param tableName    - table name
     */
    public <S> InstancesEntity saveData(DataResource<S> dataResource, String tableName) {
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

    /**
     * Deletes data with specified id.
     *
     * @param id - instances id
     */
    @Transactional
    public void deleteData(long id) {
        log.info("Starting to delete instances with id [{}]", id);
        InstancesEntity instancesEntity = getById(id);
        instancesService.deleteInstances(instancesEntity.getTableName());
        instancesRepository.deleteById(id);
        log.info("Instances [{}] has been deleted", id);
    }

    /**
     * Renames data with specified id.
     *
     * @param id      - instances id
     * @param newName - new instances name
     */
    @Transactional
    public void renameData(long id, String newName) {
        log.info("Starting to rename instances [{}] with new name [{}]", id, newName);
        InstancesEntity instancesEntity = getById(id);
        instancesService.renameInstances(instancesEntity.getTableName(), newName);
        instancesEntity.setTableName(newName);
        instancesRepository.save(instancesEntity);
        log.info("Instances [{}] has been renamed to [{}]", id, newName);
    }

    private InstancesEntity getById(long id) {
        return instancesRepository.findById(id).orElseThrow(IllegalStateException::new);
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
