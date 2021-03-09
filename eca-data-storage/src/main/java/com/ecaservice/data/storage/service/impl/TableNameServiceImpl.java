package com.ecaservice.data.storage.service.impl;

import com.ecaservice.data.storage.service.TableNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Implements service to manage table names.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class TableNameServiceImpl implements TableNameService {

    private static final String TABLE_NOT_EXISTS_QUERY_FORMAT =
            "SELECT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = '%s')";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean tableExists(String tableName) {
        Boolean result =
                jdbcTemplate.queryForObject(String.format(TABLE_NOT_EXISTS_QUERY_FORMAT, tableName), Boolean.class);
        return Boolean.TRUE.equals(result);
    }
}
