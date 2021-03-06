package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import weka.core.Instances;

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
}
