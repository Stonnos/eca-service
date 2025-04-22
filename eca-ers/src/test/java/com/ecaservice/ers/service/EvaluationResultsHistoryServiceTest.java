package com.ecaservice.ers.service;

import com.ecaservice.classifier.options.model.LogisticOptions;
import com.ecaservice.classifier.template.processor.service.ClassifierOptionsProcessor;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.ers.AbstractJpaTest;
import com.ecaservice.ers.config.ErsConfig;
import com.ecaservice.ers.mapping.ClassificationCostsReportMapperImpl;
import com.ecaservice.ers.mapping.ConfusionMatrixMapperImpl;
import com.ecaservice.ers.mapping.EvaluationResultsMapperImpl;
import com.ecaservice.ers.mapping.InstancesMapperImpl;
import com.ecaservice.ers.mapping.RocCurveReportMapperImpl;
import com.ecaservice.ers.mapping.StatisticsReportMapperImpl;
import com.ecaservice.ers.repository.EvaluationResultsInfoRepository;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.classifier.template.processor.util.Utils.toJsonString;
import static com.ecaservice.ers.TestHelperUtils.createEvaluationResultsInfo;
import static com.ecaservice.ers.dictionary.FilterDictionaries.EVALUATION_RESULTS_HISTORY_TEMPLATE;
import static com.ecaservice.ers.model.EvaluationResultsInfo_.EVALUATION_METHOD;
import static com.ecaservice.ers.model.EvaluationResultsInfo_.SAVE_DATE;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EvaluationResultsHistoryService} class.
 *
 * @author Roman Batygin
 */
@Import({EvaluationResultsHistoryService.class, EvaluationResultsMapperImpl.class, ErsConfig.class,
        ClassificationCostsReportMapperImpl.class, ConfusionMatrixMapperImpl.class, StatisticsReportMapperImpl.class,
        InstancesMapperImpl.class, RocCurveReportMapperImpl.class, EvaluationResultsHistoryCountQueryExecutor.class})
class EvaluationResultsHistoryServiceTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String RELATION_NAME = "relationName";
    private static final String INSTANCES_INFO_ID = "instancesInfo.id";
    private static final String CLASSIFIER_NAME = "classifierName";

    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private ClassifierOptionsProcessor classifierOptionsProcessor;

    @Autowired
    private EvaluationResultsInfoRepository evaluationResultsInfoRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private EvaluationResultsHistoryService evaluationResultsHistoryService;

    @Override
    public void deleteAll() {
        evaluationResultsInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Override
    public void init() {
        saveEvaluationResultsData();
        when(filterTemplateService.getGlobalFilterFields(EVALUATION_RESULTS_HISTORY_TEMPLATE)).thenReturn(
                Arrays.asList(RELATION_NAME, EVALUATION_METHOD));
    }

    /**
     * Tests global filtering by search query.
     */
    @Test
    void testGlobalFilter() {
        SortFieldRequestDto sortFieldRequestDto = new SortFieldRequestDto(SAVE_DATE, false);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Collections.singletonList(sortFieldRequestDto),
                        StringUtils.EMPTY, newArrayList());
        var pageDto = evaluationResultsHistoryService.getNextPage(pageRequestDto);
        assertThat(pageDto).isNotNull();
        assertThat(pageDto.getTotalCount()).isEqualTo(2L);
    }

    /**
     * Test filtering by classifier name.
     */
    @Test
    void testFilterByClassifierName() {
        SortFieldRequestDto sortFieldRequestDto = new SortFieldRequestDto(SAVE_DATE, false);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Collections.singletonList(sortFieldRequestDto),
                        StringUtils.EMPTY, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(CLASSIFIER_NAME, Collections.singletonList("CART"),
                        MatchMode.EQUALS));
        var pageDto = evaluationResultsHistoryService.getNextPage(pageRequestDto);
        assertThat(pageDto).isNotNull();
        assertThat(pageDto.getTotalCount()).isOne();
    }

    /**
     * Test filtering by instances id.
     */
    @Test
    void testFilterByInstancesId() {
        var instancesInfo = instancesInfoRepository.findAll().iterator().next();
        SortFieldRequestDto sortFieldRequestDto = new SortFieldRequestDto(SAVE_DATE, false);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Collections.singletonList(sortFieldRequestDto),
                        StringUtils.EMPTY, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(INSTANCES_INFO_ID,
                        Collections.singletonList(String.valueOf(instancesInfo.getId())),
                        MatchMode.EQUALS));
        var pageDto = evaluationResultsHistoryService.getNextPage(pageRequestDto);
        assertThat(pageDto).isNotNull();
        assertThat(pageDto.getTotalCount()).isOne();
    }

    private void saveEvaluationResultsData() {
        var evaluationResultsInfo1 = createEvaluationResultsInfo();
        evaluationResultsInfo1.setClassifierName("CART");
        evaluationResultsInfo1.setClassifierOptions(toJsonString(new LogisticOptions()));
        evaluationResultsInfo1.getInstancesInfo().setUuid(UUID.randomUUID().toString());
        var evaluationResultsInfo2 = createEvaluationResultsInfo();
        evaluationResultsInfo2.setClassifierName("C45");
        evaluationResultsInfo2.setClassifierOptions(toJsonString(new LogisticOptions()));
        evaluationResultsInfo2.getInstancesInfo().setUuid(UUID.randomUUID().toString());
        instancesInfoRepository.save(evaluationResultsInfo1.getInstancesInfo());
        instancesInfoRepository.save(evaluationResultsInfo2.getInstancesInfo());
        evaluationResultsInfoRepository.saveAll(Arrays.asList(evaluationResultsInfo1, evaluationResultsInfo2));
    }
}
