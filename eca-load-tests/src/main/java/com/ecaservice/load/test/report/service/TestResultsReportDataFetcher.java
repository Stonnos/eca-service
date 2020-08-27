package com.ecaservice.load.test.report.service;

import com.ecaservice.load.test.config.EcaLoadTestsConfig;
import com.ecaservice.load.test.entity.EvaluationRequestEntity;
import com.ecaservice.load.test.entity.LoadTestEntity;
import com.ecaservice.load.test.mapping.LoadTestMapper;
import com.ecaservice.load.test.report.bean.EvaluationTestBean;
import com.ecaservice.load.test.report.bean.LoadTestBean;
import com.ecaservice.load.test.repository.EvaluationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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
        fetchEvaluationTestsResults(loadTestEntity, loadTestBean);
        return loadTestBean;
    }

    private void fetchEvaluationTestsResults(LoadTestEntity loadTestEntity, LoadTestBean loadTestBean) {
        TestResultsCounter counter = new TestResultsCounter();
        List<EvaluationTestBean> evaluationTestBeans = newArrayList();
        Pageable pageRequest = PageRequest.of(0, ecaLoadTestsConfig.getPageSize());
        Page<EvaluationRequestEntity> page;
        do {
            page = evaluationRequestRepository.findByLoadTestEntity(loadTestEntity, pageRequest);
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
    }
}
