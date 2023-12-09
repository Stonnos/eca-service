package com.ecaservice.ers.service;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.ers.AbstractJpaTest;
import com.ecaservice.ers.mapping.ClassificationCostsReportMapperImpl;
import com.ecaservice.ers.mapping.ClassifierOptionsInfoMapperImpl;
import com.ecaservice.ers.mapping.ClassifierReportMapperImpl;
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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;

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
@Import({EvaluationResultsHistoryService.class, EvaluationResultsMapperImpl.class,
        ClassificationCostsReportMapperImpl.class, ConfusionMatrixMapperImpl.class, StatisticsReportMapperImpl.class,
        InstancesMapperImpl.class, RocCurveReportMapperImpl.class, ClassifierReportMapperImpl.class,
        ClassifierOptionsInfoMapperImpl.class})
public class EvaluationResultsHistoryServiceTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String INSTANCES_INFO_RELATION_NAME = "instancesInfo.relationName";
    private static final String INSTANCES_INFO_ID = "instancesInfo.id";
    private static final String CLASSIFIER_INFO_CLASSIFIER_NAME = "classifierInfo.classifierName";

    @MockBean
    private FilterTemplateService filterTemplateService;

    @Inject
    private EvaluationResultsInfoRepository evaluationResultsInfoRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;

    @Inject
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
                Arrays.asList(INSTANCES_INFO_RELATION_NAME, EVALUATION_METHOD));
    }

    /**
     * Tests global filtering by search query.
     */
    @Test
    void testGlobalFilter() {
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, SAVE_DATE, false, StringUtils.EMPTY, newArrayList());
        var pageDto = evaluationResultsHistoryService.getNextPage(pageRequestDto);
        assertThat(pageDto).isNotNull();
        assertThat(pageDto.getTotalCount()).isEqualTo(2L);
    }

    /**
     * Test filtering by classifier name.
     */
    @Test
    void testFilterByClassifierName() {
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, SAVE_DATE, false, StringUtils.EMPTY, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(CLASSIFIER_INFO_CLASSIFIER_NAME, Collections.singletonList("CART"),
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
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, SAVE_DATE, false, StringUtils.EMPTY, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(INSTANCES_INFO_ID, Collections.singletonList(String.valueOf(instancesInfo.getId())),
                        MatchMode.EQUALS));
        var pageDto = evaluationResultsHistoryService.getNextPage(pageRequestDto);
        assertThat(pageDto).isNotNull();
        assertThat(pageDto.getTotalCount()).isOne();
    }

    private void saveEvaluationResultsData() {
        var evaluationResultsInfo1 = createEvaluationResultsInfo();
        evaluationResultsInfo1.getClassifierInfo().setClassifierName("CART");
        evaluationResultsInfo1.getInstancesInfo().setDataMd5Hash(DigestUtils.md5Hex("val1"));
        var evaluationResultsInfo2 = createEvaluationResultsInfo();
        evaluationResultsInfo2.getClassifierInfo().setClassifierName("C45");
        evaluationResultsInfo2.getInstancesInfo().setDataMd5Hash(DigestUtils.md5Hex("val2"));
        instancesInfoRepository.save(evaluationResultsInfo1.getInstancesInfo());
        instancesInfoRepository.save(evaluationResultsInfo2.getInstancesInfo());
        evaluationResultsInfoRepository.saveAll(Arrays.asList(evaluationResultsInfo1, evaluationResultsInfo2));
    }
}
