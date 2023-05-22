package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.web.dto.model.AttributeDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.file.model.InstancesModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements storage service supporting concurrent operations.
 *
 * @author Roman Batygin
 */
@Slf4j
@Primary
@Service
public class ConcurrentStorageService implements StorageService {

    private final StorageService storageService;

    private final Map<String, Object> tableNamesMonitorsMap = new ConcurrentHashMap<>();

    /**
     * Constructor with spring dependency injection.
     *
     * @param storageService - storage service bean
     */
    public ConcurrentStorageService(@Qualifier("storageServiceImpl") StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public Page<InstancesEntity> getNextPage(PageRequestDto pageRequestDto) {
        return storageService.getNextPage(pageRequestDto);
    }

    @Override
    public InstancesEntity saveData(Instances instances, String tableName) {
        InstancesEntity instancesEntity;
        tableNamesMonitorsMap.putIfAbsent(tableName, new Object());
        synchronized (tableNamesMonitorsMap.get(tableName)) {
            instancesEntity = storageService.saveData(instances, tableName);
        }
        tableNamesMonitorsMap.remove(tableName);
        return instancesEntity;
    }

    @Override
    public InstancesEntity getById(long id) {
        return storageService.getById(id);
    }

    @Override
    public String renameData(long id, String newTableName) {
        String oldTableName;
        tableNamesMonitorsMap.putIfAbsent(newTableName, new Object());
        synchronized (tableNamesMonitorsMap.get(newTableName)) {
            oldTableName = storageService.renameData(id, newTableName);
        }
        tableNamesMonitorsMap.remove(newTableName);
        return oldTableName;
    }

    @Override
    public String deleteData(long id) {
        return storageService.deleteData(id);
    }

    @Override
    public PageDto<List<String>> getData(long id, PageRequestDto pageRequestDto) {
        return storageService.getData(id, pageRequestDto);
    }

    @Override
    public Instances getInstances(InstancesEntity instancesEntity) {
        return storageService.getInstances(instancesEntity);
    }

    @Override
    public InstancesModel getInstancesModelWithSelectedAttributes(InstancesEntity instancesEntity) {
        return storageService.getInstancesModelWithSelectedAttributes(instancesEntity);
    }

    @Override
    public List<AttributeDto> getAttributes(long id) {
        return storageService.getAttributes(id);
    }

    @Override
    public AttributeEntity setClassAttribute(long classAttributeId) {
        return storageService.setClassAttribute(classAttributeId);
    }

    @Override
    public InstancesEntity selectAllAttributes(long id) {
        return storageService.selectAllAttributes(id);
    }
}
