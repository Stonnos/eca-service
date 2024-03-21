package com.ecaservice.core.data.cleaner.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Table data cleaner properties.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@Configuration
@ConfigurationProperties("table.data.clean")
public class TableDataCleanerProperties {

    /**
     * Table data cleaner enabled?
     */
    private boolean enabled;

    /**
     * Cron expression for scheduler
     */
    private String cronExpression;

    /**
     * Tables properties
     */
    @Valid
    private List<TableCleanProperties> tables = newArrayList();

    /**
     * Table clean properties.
     */
    @Data
    public static class TableCleanProperties {

        private static final long DEFAULT_STORAGE_PERIOD_IN_DAYS = 30L;

        /**
         * Table name
         */
        @NotEmpty
        private String tableName;

        /**
         * Creation timestamp column name
         */
        @NotEmpty
        private String creationTimestampColumnName;

        /**
         * Table data storage period in days
         */
        private Long storagePeriodInDays = DEFAULT_STORAGE_PERIOD_IN_DAYS;
    }
}
