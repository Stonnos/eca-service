package com.ecaservice.load.test.report.service;

import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.mapping.LoadTestMapper;
import com.ecaservice.load.test.report.bean.LoadTestBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.ecaservice.load.test.util.Utils.tps;
import static com.ecaservice.test.common.util.Utils.totalTime;

/**
 * Test results report data fetcher.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestResultsReportDataFetcher {

    private final LoadTestMapper loadTestMapper;

    /**
     * Fetch test results report data for specified test.
     *
     * @param loadTestEntity - load test entity
     * @return load test bean
     */
    public LoadTestBean fetchReportData(LoadTestEntity loadTestEntity) {
        LoadTestBean loadTestBean = loadTestMapper.mapToBean(loadTestEntity);
        String totalTime = totalTime(loadTestEntity.getStarted(), loadTestEntity.getFinished());
        BigDecimal tps =
                tps(loadTestEntity.getStarted(), loadTestEntity.getFinished(), loadTestEntity.getNumRequests());
        loadTestBean.setTotalTime(totalTime);
        loadTestBean.setTps(tps);
        return loadTestBean;
    }
}
