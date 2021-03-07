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
public class StorageConcurrentService implements StorageService {

    private final StorageService storageService;

    private final Map<String, Object> tableNamesMonitorsMap = new ConcurrentHashMap<>();

    public StorageConcurrentService(@Qualifier("storageServiceImpl") StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public Page<InstancesEntity> getNextPage(PageRequestDto pageRequestDto) {
        return storageService.getNextPage(pageRequestDto);
    }

    @Override
    public InstancesEntity saveData(Instances instances, String tableName) {
        String trimmedName = tableName.trim();
        InstancesEntity instancesEntity;
        tableNamesMonitorsMap.putIfAbsent(trimmedName, new Object());
        synchronized (tableNamesMonitorsMap.get(trimmedName)) {
            instancesEntity = storageService.saveData(instances, tableName);
        }
        tableNamesMonitorsMap.remove(trimmedName);
        return instancesEntity;
    }

    @Override
    public void renameData(long id, String newTableName) {
        String trimmedName = newTableName.trim();
        tableNamesMonitorsMap.putIfAbsent(trimmedName, new Object());
        synchronized (tableNamesMonitorsMap.get(trimmedName)) {
            storageService.renameData(id, newTableName);
        }
        tableNamesMonitorsMap.remove(trimmedName);
    }

    @Override
    public void deleteData(long id) {
        storageService.deleteData(id);
    }
}
