package com.ecaservice.load.test.report.service;

import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.report.bean.LoadTestBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Test results report data fetcher.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestResultsReportDataFetcher {

    /**
     * Fetch test results report data for specified test.
     *
     * @param loadTestEntity - load test entity
     * @return load test bean
     */
    public LoadTestBean fetchReportData(LoadTestEntity loadTestEntity) {
        return null;
    }
}
