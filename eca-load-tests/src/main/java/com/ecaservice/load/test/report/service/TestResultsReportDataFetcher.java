package com.ecaservice.load.test.report.service;

import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.mapping.LoadTestMapper;
import com.ecaservice.load.test.report.bean.EvaluationTestBean;
import com.ecaservice.load.test.report.bean.LoadTestBean;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import com.ecaservice.test.common.report.TestResultsCounter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.load.test.util.Utils.tps;
import static com.ecaservice.test.common.util.Utils.totalTime;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Test results report data fetcher.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TestResultsReportDataFetcher {

    private final EcaLoadTestsConfig ecaLoadTestsConfig;
    private final LoadTestMapper loadTestMapper;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Fetch test results report data for specified test.
     *
     * @param loadTestEntity - load test entity
     * @return load test bean
     */
    public LoadTestBean fetchReportData(LoadTestEntity loadTestEntity) {
        LoadTestBean loadTestBean = loadTestMapper.mapToBean(loadTestEntity);
        LocalDateTime started =
                evaluationRequestRepository.getMinStartedDate(loadTestEntity).orElse(loadTestEntity.getStarted());
        LocalDateTime finished =
                evaluationRequestRepository.getMaxFinishedDate(loadTestEntity).orElse(loadTestEntity.getFinished());
        loadTestBean.setStarted(loadTestMapper.formatLocalDateTime(started));
        loadTestBean.setFinished(loadTestMapper.formatLocalDateTime(finished));
        loadTestBean.setTotalTime(totalTime(started, finished));
        loadTestBean.setTps(tps(started, finished, loadTestEntity.getNumRequests()));
        fetchEvaluationTestsResults(loadTestEntity, loadTestBean);
        return loadTestBean;
    }

    private void fetchEvaluationTestsResults(LoadTestEntity loadTestEntity, LoadTestBean loadTestBean) {
        TestResultsCounter counter = new TestResultsCounter();
        List<EvaluationTestBean> evaluationTestBeans = newArrayList();
        Pageable pageRequest = PageRequest.of(0, ecaLoadTestsConfig.getPageSize());
        Page<EvaluationRequestEntity> page;
        do {
            page = evaluationRequestRepository.findByLoadTestEntityOrderByStarted(loadTestEntity, pageRequest);
            if (page == null || !page.hasContent()) {
                break;
            } else {
                for (EvaluationRequestEntity evaluationRequestEntity : page.getContent()) {
                    evaluationRequestEntity.getTestResult().apply(counter);
                    evaluationTestBeans.add(loadTestMapper.map(evaluationRequestEntity));
                }
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
        loadTestBean.setEvaluationTests(evaluationTestBeans);
        loadTestBean.setPassedCount(counter.getPassed());
        loadTestBean.setFailedCount(counter.getFailed());
        loadTestBean.setErrorCount(counter.getErrors());
        loadTestBean.setTotal(evaluationTestBeans.size());
    }
}
