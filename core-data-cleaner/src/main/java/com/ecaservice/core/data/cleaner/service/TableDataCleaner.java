package com.ecaservice.core.data.cleaner.service;

import com.ecaservice.core.data.cleaner.config.TableDataCleanerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Table data cleaner.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TableDataCleaner {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String DELETE_QUERY_FORMAT = "delete from %s where %s < ?";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Cleans old rows from specified table.
     *
     * @param tableCleanProperties - table clean properties
     */
    @Transactional
    public void clean(TableDataCleanerProperties.TableCleanProperties tableCleanProperties) {
        LocalDateTime timestamp = LocalDateTime.now().minusDays(tableCleanProperties.getStoragePeriodInDays());
        log.info("Starting to delete data from table [{}] with creation timestamp less than [{}]",
                tableCleanProperties, DATE_TIME_FORMATTER.format(timestamp));
        String deleteQuery = String.format(DELETE_QUERY_FORMAT, tableCleanProperties.getTableName(),
                tableCleanProperties.getCreationTimestampColumnName());
        int deleted = jdbcTemplate.update(deleteQuery, Timestamp.valueOf(timestamp));
        log.info("[{}] rows has been deleted from table [{}] with creation timestamp less than [{}]",
                deleted, tableCleanProperties, DATE_TIME_FORMATTER.format(timestamp));
    }
}
