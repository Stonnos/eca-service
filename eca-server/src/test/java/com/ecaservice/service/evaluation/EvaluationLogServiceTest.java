package com.ecaservice.service.evaluation;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.config.CommonConfig;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationLog_;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.AbstractJpaTest;
import com.ecaservice.service.filter.GlobalFilterService;
import com.ecaservice.web.dto.model.FilterFieldType;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.ID3;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link EvaluationLogService} functionality.
 *
 * @author Roman Batygin
 */
@Import(CommonConfig.class)
public class EvaluationLogServiceTest extends AbstractJpaTest {

    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private CommonConfig commonConfig;

    @Mock
    private GlobalFilterService globalFilterService;

    private EvaluationLogService evaluationLogService;

    @Override
    public void init() {
        evaluationLogService = new EvaluationLogService(commonConfig, globalFilterService, evaluationLogRepository);
    }

    @Override
    public void deleteAll() {
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
        PageRequestDto pageRequestDto =
                new PageRequestDto(0, 10, EvaluationLog_.CLASSIFIER_NAME, false, null, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(EvaluationLog_.EVALUATION_STATUS, RequestStatus.FINISHED.name(),
                        FilterFieldType.REFERENCE, MatchMode.EQUALS));
        pageRequestDto.getFilters().add(
                new FilterRequestDto(EvaluationLog_.CLASSIFIER_NAME, "C", FilterFieldType.TEXT, MatchMode.LIKE));
        Page<EvaluationLog> evaluationLogPage = evaluationLogService.getNextPage(pageRequestDto);
        List<EvaluationLog> evaluationLogs = evaluationLogPage.getContent();
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalElements()).isEqualTo(2);
        assertThat(evaluationLogs.size()).isEqualTo(2);
        assertThat(evaluationLogs.get(0).getRequestId()).isEqualTo(evaluationLog.getRequestId());
        assertThat(evaluationLogs.get(1).getRequestId()).isEqualTo(evaluationLog1.getRequestId());
    }

    @Test
    public void testFilterByRelationName() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        evaluationLog.setInstancesInfo(TestHelperUtils.createInstancesInfo());
        evaluationLog.getInstancesInfo().setRelationName("Data");
        evaluationLogRepository.save(evaluationLog);
        EvaluationLog evaluationLog1 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        evaluationLog1.setInstancesInfo(TestHelperUtils.createInstancesInfo());
        evaluationLog1.getInstancesInfo().setRelationName("Relation");
        evaluationLogRepository.save(evaluationLog1);
        PageRequestDto pageRequestDto =
                new PageRequestDto(0, 10, EvaluationLog_.CLASSIFIER_NAME, false, null, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto("instancesInfo.relationName", "Dat", FilterFieldType.TEXT,
                        MatchMode.LIKE));
        Page<EvaluationLog> evaluationLogPage = evaluationLogService.getNextPage(pageRequestDto);
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalElements()).isOne();
    }

    /**
     * Tests global filtering by "car" search query and evaluation status equals to FINISHED.
     */
    @Test
    public void testGlobalFilter() {
        EvaluationLog evaluationLog =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        evaluationLog.setClassifierName(CART.class.getSimpleName());
        evaluationLog.setInstancesInfo(TestHelperUtils.createInstancesInfo());
        EvaluationLog evaluationLog1 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.ERROR);
        evaluationLog1.setClassifierName(CART.class.getSimpleName());
        evaluationLog1.setInstancesInfo(TestHelperUtils.createInstancesInfo());
        EvaluationLog evaluationLog2 =
                TestHelperUtils.createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        evaluationLog2.setClassifierName(ID3.class.getSimpleName());
        evaluationLog2.setInstancesInfo(TestHelperUtils.createInstancesInfo());
        evaluationLogRepository.saveAll(Arrays.asList(evaluationLog, evaluationLog1, evaluationLog2));
        PageRequestDto pageRequestDto =
                new PageRequestDto(0, 10, EvaluationLog_.CREATION_DATE, false, "car", newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(EvaluationLog_.EVALUATION_STATUS, RequestStatus.FINISHED.name(),
                        FilterFieldType.REFERENCE,
                        MatchMode.EQUALS));
        when(globalFilterService.getGlobalFilterFields(FilterTemplateType.EVALUATION_LOG)).thenReturn(
                Arrays.asList(EvaluationLog_.CLASSIFIER_NAME, EvaluationLog_.REQUEST_ID, "instancesInfo.relationName"));
        Page<EvaluationLog> evaluationLogPage = evaluationLogService.getNextPage(pageRequestDto);
        assertThat(evaluationLogPage).isNotNull();
        assertThat(evaluationLogPage.getTotalElements()).isOne();
    }
}
