package com.ecaservice.core.data.cleaner.scheduler;

import com.ecaservice.core.data.cleaner.config.TableDataCleanerProperties;
import com.ecaservice.core.data.cleaner.service.TableDataCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Scheduler for clean temporary tables data.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TableDataCleanScheduler {

    private final TableDataCleanerProperties tableDataCleanerProperties;
    private final TableDataCleaner tableDataCleaner;

    /**
     * Initialization method.
     */
    @PostConstruct
    public void init() {
        log.info("Clean temporary tables data scheduler has been configured. Tables to clean: {}",
                tableDataCleanerProperties.getTables());
    }

    /**
     * Cleans temporary tables data. By default job runs each day midnight.
     */
    @Scheduled(cron = "${table.data.clean.cronExpression:0 0 0 * * *}")
    public void clean() {
        log.info("Starting job to clean temporary tables data");
        tableDataCleanerProperties.getTables().forEach(tableDataCleaner::clean);
        log.info("Temporary tables data clean job has been finished");
    }
}
