package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.config.EcaDsConfig;
import eca.data.db.SqlQueryHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import weka.core.Instances;

/**
 * Instances migration service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesService {

    private final JdbcTemplate jdbcTemplate;
    private final TransactionalService transactionalMigrationService;
    private final EcaDsConfig ecaDsConfig;
    private final SqlQueryHelper sqlQueryHelper;

    /**
     * Saves training data into database.
     *
     * @param tableName - training data table name
     * @param instances - training data
     */
    public void saveInstances(String tableName, Instances instances) {
        log.info("Starting to save instances '{}' into table '{}'.", instances.relationName(), tableName);
        log.info("Starting to create table '{}'.", tableName);
        String createTableQuery = sqlQueryHelper.buildCreateTableQuery(tableName, instances);
        log.trace("create table query: {}", createTableQuery);
        jdbcTemplate.execute(createTableQuery);
        log.info("Table '{}' has been successfully created.", tableName);

        log.info("Starting to save data into table '{}'.", tableName);
        int batchSize = ecaDsConfig.getBatchSize();
        for (int offset = 0; offset < instances.numInstances(); offset += batchSize) {
            log.trace("Starting to save batch with limit = {}, offset = {} into table '{}'.", batchSize, offset,
                    tableName);
            transactionalMigrationService.saveBatch(tableName, instances, batchSize, offset);
            log.trace("{} rows has been saved into table '{}'.", offset + batchSize, tableName);
        }
        log.info("Data has been saved into table '{}'.", tableName);
        log.info("Data saving has been successfully completed. Instances '{}' has been saved into table '{}.",
                instances.relationName(), tableName);
    }
}
