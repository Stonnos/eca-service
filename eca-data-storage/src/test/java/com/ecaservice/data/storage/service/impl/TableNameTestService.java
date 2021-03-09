package com.ecaservice.data.storage.service.impl;

import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.TableNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implements table name service for h2 database.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class TableNameTestService implements TableNameService {

    private final InstancesRepository instancesRepository;

    @Override
    public boolean tableExists(String tableName) {
        return instancesRepository.findAll().stream()
                .anyMatch(instancesEntity -> instancesEntity.getTableName().equals(tableName));
    }
}
