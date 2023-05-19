package com.ecaservice.data.storage.service;

import eca.data.db.SqlQueryHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.core.Instances;

/**
 * Instances batch service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesBatchService {

    private final JdbcTemplate jdbcTemplate;
    private final SqlQueryHelper sqlQueryHelper;

    /**
     * Saves training data batch into database.
     *
     * @param tableName - table name
     * @param instances - training data
     * @param limit     - batch size
     * @param offset    - offset value
     */
    @Transactional
    public void saveBatch(String tableName, Instances instances, int limit, int offset) {
        String sqlQuery = sqlQueryHelper.buildPreparedInsertQuery(tableName, instances);
        for (int i = offset; i < Integer.min(instances.numInstances(), limit + offset); i++) {
            Object[] args = sqlQueryHelper.prepareQueryParameters(instances.instance(i));
            jdbcTemplate.update(sqlQuery, args);
        }
    }
}
