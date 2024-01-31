package com.ecaservice.ers.report;

import com.ecaservice.classifier.options.model.LogisticOptions;
import com.ecaservice.classifier.template.processor.service.ClassifierOptionsProcessor;
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
import com.ecaservice.ers.service.EvaluationResultsHistoryService;
import com.ecaservice.report.model.FilterBean;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.MultiSortPageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;

import static com.ecaservice.classifier.template.processor.util.Utils.toJsonString;
import static com.ecaservice.ers.TestHelperUtils.createEvaluationResultsInfo;
import static com.ecaservice.ers.TestHelperUtils.createFilterDictionaryDto;
import static com.ecaservice.ers.TestHelperUtils.loadEvaluationResultsHistoryFilterFields;
import static com.ecaservice.ers.dictionary.FilterDictionaries.CLASSIFIER_NAME;
import static com.ecaservice.ers.model.EvaluationResultsInfo_.SAVE_DATE;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks {@link EvaluationResultsHistoryReportDataFetcher} functionality.
 *
 * @author Roman Batygin
 */
@Import({EvaluationResultsHistoryService.class, EvaluationResultsMapperImpl.class,
        ClassificationCostsReportMapperImpl.class, ConfusionMatrixMapperImpl.class, StatisticsReportMapperImpl.class,
        InstancesMapperImpl.class, RocCurveReportMapperImpl.class, ClassifierReportMapperImpl.class,
        ClassifierOptionsInfoMapperImpl.class, EvaluationResultsHistoryReportDataFetcher.class})
class EvaluationResultsHistoryReportDataFetcherTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String INSTANCES_INFO_ID = "instancesInfo.id";

    @MockBean
    private FilterTemplateService filterTemplateService;

    @MockBean
    private ClassifierOptionsProcessor classifierOptionsProcessor;

    @Inject
    private EvaluationResultsInfoRepository evaluationResultsInfoRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;

    @Inject
    private EvaluationResultsHistoryReportDataFetcher evaluationResultsHistoryReportDataFetcher;

    @Override
    public void init() {
        saveEvaluationResultsData();
        when(filterTemplateService.getFilterFields(anyString())).thenReturn(loadEvaluationResultsHistoryFilterFields());
        when(filterTemplateService.getFilterDictionary(CLASSIFIER_NAME)).thenReturn(createFilterDictionaryDto());
    }

    @Override
    public void deleteAll() {
        evaluationResultsInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testFetchEvaluationResultsHistoryReportData() {
        var instancesInfo = instancesInfoRepository.findAll().iterator().next();
        SortFieldRequestDto sortFieldRequestDto = new SortFieldRequestDto(SAVE_DATE, false);
        MultiSortPageRequestDto pageRequestDto =
                new MultiSortPageRequestDto(PAGE_NUMBER, PAGE_SIZE, Collections.singletonList(sortFieldRequestDto),
                        StringUtils.EMPTY, newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(INSTANCES_INFO_ID,
                        Collections.singletonList(String.valueOf(instancesInfo.getId())),
                        MatchMode.EQUALS));
        var reportData = evaluationResultsHistoryReportDataFetcher.fetchReportData(pageRequestDto);
        assertThat(reportData).isNotNull();
        assertThat(reportData.getTotalPages()).isOne();
        assertThat(reportData.getItems()).hasSize(1);
        assertThat(reportData.getFilters()).hasSize(1);
        FilterBean instancesFilterBean = reportData.getFilters().iterator().next();
        assertThat(instancesFilterBean.getValue1()).isEqualTo(instancesInfo.getRelationName());
    }

    private void saveEvaluationResultsData() {
        var evaluationResultsInfo1 = createEvaluationResultsInfo();
        evaluationResultsInfo1.getClassifierInfo().setClassifierName("CART");
        evaluationResultsInfo1.getClassifierInfo().setOptions(toJsonString(new LogisticOptions()));
        evaluationResultsInfo1.getInstancesInfo().setDataMd5Hash(DigestUtils.md5Hex("val1"));
        var evaluationResultsInfo2 = createEvaluationResultsInfo();
        evaluationResultsInfo2.getClassifierInfo().setClassifierName("C45");
        evaluationResultsInfo2.getClassifierInfo().setOptions(toJsonString(new LogisticOptions()));
        evaluationResultsInfo2.getInstancesInfo().setDataMd5Hash(DigestUtils.md5Hex("val2"));
        instancesInfoRepository.save(evaluationResultsInfo1.getInstancesInfo());
        instancesInfoRepository.save(evaluationResultsInfo2.getInstancesInfo());
        evaluationResultsInfoRepository.saveAll(Arrays.asList(evaluationResultsInfo1, evaluationResultsInfo2));
    }
}
