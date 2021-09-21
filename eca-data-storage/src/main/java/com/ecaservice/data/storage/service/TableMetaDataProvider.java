package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.model.ColumnModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Database tables meta data provider.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TableMetaDataProvider {

    private static final String PUBLIC_SCHEMA = "public";
    private static final int COLUMN_NAME_IDX = 1;
    private static final int DATA_TYPE_INDEX = 2;

    private static final String COLUMNS_META_DATA_QUERY =
            "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Gets table columns info.
     *
     * @param tableName - table name
     * @return table columns info
     */
    public List<ColumnModel> getTableColumns(String tableName) {
        log.info("Starting to fetch table [{}] meta data", tableName);
        var result = jdbcTemplate.query(COLUMNS_META_DATA_QUERY,
                new Object[] {PUBLIC_SCHEMA, tableName},
                (resultSet, rowNum) -> ColumnModel.builder()
                        .columnName(resultSet.getString(COLUMN_NAME_IDX))
                        .dataType(resultSet.getString(DATA_TYPE_INDEX))
                        .build());
        log.info("Table [{}] meta data has been fetched", tableName);
        return result;
    }
}
