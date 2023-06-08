package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.model.InstancesBatchOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Saves training data batch into database.
     *
     * @param instancesBatchOptions - instances batch options
     */
    @Transactional
    public void saveBatch(InstancesBatchOptions instancesBatchOptions) {
        var instances = instancesBatchOptions.getInstances();
        var sqlQueryHelper = instancesBatchOptions.getSqlQueryHelper();
        String sqlQuery = sqlQueryHelper.buildPreparedInsertQuery(instancesBatchOptions.getTableName(), instances);
        int offset = instancesBatchOptions.getOffset();
        int limit = instancesBatchOptions.getLimit();
        for (int i = offset; i < Integer.min(instances.numInstances(), limit + offset); i++) {
            sqlQueryHelper.setNextId(i);
            Object[] args = sqlQueryHelper.prepareQueryParameters(instances.instance(i));
            jdbcTemplate.update(sqlQuery, args);
        }
    }
}
