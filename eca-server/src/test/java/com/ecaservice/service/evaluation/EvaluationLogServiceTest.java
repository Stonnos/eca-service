package com.ecaservice.service.evaluation;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.web.dto.FilterRequestDto;
import com.ecaservice.web.dto.FilterType;
import com.ecaservice.web.dto.MatchMode;
import com.ecaservice.web.dto.PageRequestDto;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.trees.C45;
import eca.trees.CART;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link EvaluationLogService} functionality.
 *
 * @author Roman Batygin
 */
@Import(EvaluationLogService.class)
public class EvaluationLogServiceTest extends AbstractJpaTest {

    @Inject
    private EvaluationLogService evaluationLogService;
    @Inject
    private EvaluationLogRepository evaluationLogRepository;

    @Before
    public void init() {
        evaluationLogRepository.deleteAll();
    }

    @Test
    public void testRequestsStatusesStatisticsCalculation() {
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.NEW));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.NEW));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.TIMEOUT));
        evaluationLogRepository.save(
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED));
        Map<RequestStatus, Long> requestStatusesMap = evaluationLogService.getRequestStatusesStatistics();
        assertThat(requestStatusesMap).isNotNull();
        assertThat(requestStatusesMap.size()).isEqualTo(RequestStatus.values().length);
        assertThat(requestStatusesMap.get(RequestStatus.NEW)).isEqualTo(2L);
        assertThat(requestStatusesMap.get(RequestStatus.FINISHED)).isOne();
        assertThat(requestStatusesMap.get(RequestStatus.ERROR)).isEqualTo(3L);
        assertThat(requestStatusesMap.get(RequestStatus.TIMEOUT)).isOne();
    }

    /**
     * Tests filter by request status and classifier name order by classifier name.
     */
    @Test
    public void testFilterByStatusAndClassifierNameLikeOrderByClassifierName() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        evaluationLog.setClassifierName(CART.class.getSimpleName());
        evaluationLogRepository.save(evaluationLog);
        EvaluationLog evaluationLog1 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        evaluationLog1.setClassifierName(C45.class.getSimpleName());
        evaluationLogRepository.save(evaluationLog1);
        EvaluationLog evaluationLog2 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR);
        evaluationLog2.setClassifierName(CART.class.getSimpleName());
        evaluationLogRepository.save(evaluationLog2);
        EvaluationLog evaluationLog3 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        evaluationLog3.setClassifierName(KNearestNeighbours.class.getSimpleName());
        evaluationLogRepository.save(evaluationLog3);
        EvaluationLog evaluationLog4 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR);
        evaluationLog4.setClassifierName(NeuralNetwork.class.getSimpleName());
        evaluationLogRepository.save(evaluationLog4);
        PageRequestDto pageRequestDto = new PageRequestDto(0, 10, "classifierName", false, new ArrayList<>());
        pageRequestDto.getFilters().add(
                new FilterRequestDto("evaluationStatus", RequestStatus.FINISHED.name(), FilterType.REFERENCE,
                        MatchMode.EQUALS));
        pageRequestDto.getFilters().add(new FilterRequestDto("classifierName", "C", FilterType.TEXT,
                MatchMode.LIKE));
        Page<EvaluationLog> evaluationLogPage = evaluationLogService.getNextPage(pageRequestDto);
        List<EvaluationLog> evaluationLogs = evaluationLogPage.getContent();
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalElements()).isEqualTo(2);
        assertThat(evaluationLogs.size()).isEqualTo(2);
        assertThat(evaluationLogs.get(0).getRequestId()).isEqualTo(evaluationLog.getRequestId());
        assertThat(evaluationLogs.get(1).getRequestId()).isEqualTo(evaluationLog1.getRequestId());
    }
}
