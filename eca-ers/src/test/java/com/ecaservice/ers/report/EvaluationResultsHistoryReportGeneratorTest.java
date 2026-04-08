package com.ecaservice.ers.report;

import com.ecaservice.classifier.options.model.LogisticOptions;
import com.ecaservice.classifier.template.processor.config.ClassifiersTemplateProperties;
import com.ecaservice.classifier.template.processor.service.ClassifierOptionsProcessor;
import com.ecaservice.classifier.template.processor.service.ClassifiersTemplateProvider;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.ers.AbstractJpaTest;
import com.ecaservice.ers.config.ErsConfig;
import com.ecaservice.ers.mapping.ClassificationCostsReportMapperImpl;
import com.ecaservice.ers.mapping.ConfusionMatrixMapperImpl;
import com.ecaservice.ers.mapping.EvaluationResultsMapperImpl;
import com.ecaservice.ers.mapping.InstancesMapperImpl;
import com.ecaservice.ers.mapping.RocCurveReportMapperImpl;
import com.ecaservice.ers.mapping.StatisticsReportMapperImpl;
import com.ecaservice.ers.report.model.EvaluationResultsHistoryBean;
import com.ecaservice.ers.repository.EvaluationResultsInfoRepository;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import com.ecaservice.ers.service.EvaluationResultsHistoryCountQueryExecutor;
import com.ecaservice.ers.service.EvaluationResultsHistoryService;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.FilterBean;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.FormTemplateGroupDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import lombok.Cleanup;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ecaservice.classifier.template.processor.util.Utils.toJsonString;
import static com.ecaservice.ers.TestHelperUtils.createDecisionTreeOptions;
import static com.ecaservice.ers.TestHelperUtils.createEvaluationResultsInfo;
import static com.ecaservice.ers.TestHelperUtils.createFilterDictionaryDto;
import static com.ecaservice.ers.TestHelperUtils.loadClassifiersTemplates;
import static com.ecaservice.ers.TestHelperUtils.loadEvaluationResultsHistoryFilterFields;
import static com.ecaservice.ers.dictionary.FilterDictionaries.CLASSIFIER_NAME;
import static com.ecaservice.ers.model.EvaluationResultsInfo_.SAVE_DATE;
import static com.ecaservice.ers.report.ReportTemplates.EVALUATION_RESULTS_HISTORY_TEMPLATE_CODE;
import static com.ecaservice.report.ReportGenerator.generateReport;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks {@link EvaluationResultsHistoryReportDataFetcher} functionality.
 *
 * @author Roman Batygin
 */
@Import({EvaluationResultsHistoryService.class, EvaluationResultsMapperImpl.class,
        ClassificationCostsReportMapperImpl.class, ConfusionMatrixMapperImpl.class,
        ClassifierOptionsProcessor.class, ClassifiersTemplateProperties.class, ClassifiersTemplateProvider.class,
        StatisticsReportMapperImpl.class, EvaluationResultsHistoryCountQueryExecutor.class, ErsConfig.class,
        InstancesMapperImpl.class, RocCurveReportMapperImpl.class, EvaluationResultsHistoryReportDataFetcher.class})
class EvaluationResultsHistoryReportGeneratorTest extends AbstractJpaTest {

    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;
    private static final String INSTANCES_INFO_ID = "instancesInfo.id";
    private static final String CART_LABEL = "Алгоритм CART";
    private static final String CART_VALUE = "CART";
    private static final String CLASSIFIERS = "classifiers";

    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private FormTemplateProvider formTemplateProvider;

    @Autowired
    private EvaluationResultsInfoRepository evaluationResultsInfoRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private EvaluationResultsHistoryReportDataFetcher evaluationResultsHistoryReportDataFetcher;

    @Override
    public void init() {
        saveEvaluationResultsData();
        when(filterTemplateService.getFilterFields(anyString())).thenReturn(loadEvaluationResultsHistoryFilterFields());
        when(filterTemplateService.getFilterDictionary(CLASSIFIER_NAME)).thenReturn(
                createFilterDictionaryDto(
                        List.of(new FilterDictionaryValueDto(CART_LABEL, CART_VALUE))
                )
        );
        FormTemplateGroupDto templates = loadClassifiersTemplates();
        when(formTemplateProvider.getFormGroupDto(CLASSIFIERS)).thenReturn(templates);
    }

    @Override
    public void deleteAll() {
        evaluationResultsInfoRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testGenerateEvaluationResultsHistoryReportData() throws IOException {
        var instancesInfo = instancesInfoRepository.findAll().iterator().next();
        SortFieldRequestDto sortFieldRequestDto = new SortFieldRequestDto(SAVE_DATE, false);
        PageRequestDto pageRequestDto =
                new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Collections.singletonList(sortFieldRequestDto),
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

        testGenerateReport(reportData);
    }

    private void saveEvaluationResultsData() {
        var evaluationResultsInfo1 = createEvaluationResultsInfo();
        evaluationResultsInfo1.setClassifierName("CART");
        evaluationResultsInfo1.setClassifierOptions(toJsonString(createDecisionTreeOptions()));
        evaluationResultsInfo1.getInstancesInfo().setUuid(UUID.randomUUID().toString());
        var evaluationResultsInfo2 = createEvaluationResultsInfo();
        evaluationResultsInfo2.setClassifierName("C45");
        evaluationResultsInfo2.setClassifierOptions(toJsonString(new LogisticOptions()));
        evaluationResultsInfo2.getInstancesInfo().setUuid(UUID.randomUUID().toString());
        instancesInfoRepository.save(evaluationResultsInfo1.getInstancesInfo());
        instancesInfoRepository.save(evaluationResultsInfo2.getInstancesInfo());
        evaluationResultsInfoRepository.saveAll(Arrays.asList(evaluationResultsInfo1, evaluationResultsInfo2));
    }

    private void testGenerateReport(BaseReportBean<EvaluationResultsHistoryBean> baseReportBean) throws IOException {
        String fileName = String.format("%s/target/evaluation-results-history-report.xlsx", USER_DIR);
        @Cleanup var outputStream = new FileOutputStream(fileName);
        generateReport(EVALUATION_RESULTS_HISTORY_TEMPLATE_CODE, baseReportBean, outputStream);
    }
}
