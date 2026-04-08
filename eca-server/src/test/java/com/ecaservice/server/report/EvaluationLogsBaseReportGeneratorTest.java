package com.ecaservice.server.report;

import com.ecaservice.classifier.template.processor.config.ClassifiersTemplateProperties;
import com.ecaservice.classifier.template.processor.service.ClassifierOptionsProcessor;
import com.ecaservice.classifier.template.processor.service.ClassifiersTemplateProvider;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.EvaluationLog_;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.report.model.EvaluationLogBean;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.EvaluationResultsRequestEntityRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractJpaTest;
import com.ecaservice.server.service.classifiers.ClassifierOptionsInfoProcessor;
import com.ecaservice.server.service.ers.ErsService;
import com.ecaservice.server.service.evaluation.EvaluationLogCountQueryExecutor;
import com.ecaservice.server.service.evaluation.EvaluationLogDataService;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.FormTemplateGroupDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import com.google.common.collect.ImmutableList;
import eca.ensemble.StackingClassifier;
import jakarta.persistence.EntityManager;
import lombok.Cleanup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.report.ReportGenerator.generateReport;
import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.PAGE_SIZE;
import static com.ecaservice.server.TestHelperUtils.createFilterDictionaryDto;
import static com.ecaservice.server.TestHelperUtils.loadClassifiersTemplates;
import static com.ecaservice.server.TestHelperUtils.loadEnsembleClassifiersTemplates;
import static com.ecaservice.server.service.filter.dictionary.FilterDictionaries.CLASSIFIER_NAME;
import static com.ecaservice.server.util.ClassifierOptionsHelper.toJsonString;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests that checks EvaluationLogsBaseReportDataFetcher functionality {@see EvaluationLogsBaseReportDataFetcher}.
 *
 * @author Roman Batygin
 */
@Import({AppProperties.class, EvaluationLogMapperImpl.class, EvaluationLogDataService.class,
        InstancesInfoMapperImpl.class, DateTimeConverter.class, ClassifiersProperties.class,
        EvaluationLogCountQueryExecutor.class, ClassifierOptionsInfoProcessor.class, ClassifierOptionsProcessor.class,
        ClassifiersTemplateProperties.class, ClassifiersTemplateProvider.class,
        EvaluationLogsBaseReportDataFetcher.class})
class EvaluationLogsBaseReportGeneratorTest extends AbstractJpaTest {

    private static final List<String> DATE_RANGE_VALUES = ImmutableList.of("2018-01-01", "2018-01-07");
    private static final LocalDateTime CREATION_DATE = LocalDateTime.of(2018, 1, 5, 0, 0, 0);
    private static final String EVALUATION_LOGS_REPORT_TEMPLATE_XLSX = "evaluation-logs-report-template.xlsx";
    private static final String CART_LABEL = "Алгоритм CART";
    private static final String CART_VALUE = "CART";
    private static final String STACKING_LABEL = "Алгоритм Stacking";
    private static final String STACKING_VALUE = "StackingClassifier";
    private static final String CLASSIFIERS = "classifiers";
    private static final String ENSEMBLE_CLASSIFIERS = "ensembleClassifiers";

    @MockBean
    private FilterTemplateService filterTemplateService;
    @MockBean
    private ErsService ersService;
    @MockBean
    private ObjectStorageService objectStorageService;
    @MockBean
    private FormTemplateProvider formTemplateProvider;

    @Autowired
    private EvaluationLogCountQueryExecutor evaluationLogCountQueryExecutor;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private ClassifiersProperties classifiersProperties;
    @Autowired
    private EvaluationLogMapper evaluationLogMapper;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private EvaluationLogRepository evaluationLogRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;
    @Autowired
    private EvaluationResultsRequestEntityRepository evaluationResultsRequestEntityRepository;
    @Autowired
    private ClassifierOptionsInfoProcessor classifierOptionsInfoProcessor;
    @Autowired
    private EvaluationLogsBaseReportDataFetcher evaluationLogsBaseReportDataFetcher;

    @Override
    public void init() {
        when(filterTemplateService.getFilterDictionary(CLASSIFIER_NAME)).thenReturn(
                createFilterDictionaryDto(
                        List.of(
                                new FilterDictionaryValueDto(CART_LABEL, CART_VALUE),
                                new FilterDictionaryValueDto(STACKING_LABEL, STACKING_VALUE)
                        )
                )
        );
        mockClassifiersFormTemplates();
    }

    @Override
    public void deleteAll() {
        evaluationLogRepository.deleteAll();
        instancesInfoRepository.deleteAll();
    }

    @Test
    void testGenerateEvaluationLogsData() throws IOException {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog();
        evaluationLog.setCreationDate(CREATION_DATE);
        evaluationLog.setRequestStatus(RequestStatus.FINISHED);
        instancesInfoRepository.save(evaluationLog.getInstancesInfo());
        evaluationLogRepository.save(evaluationLog);

        EvaluationLog evaluationLogStacking = TestHelperUtils.createEvaluationLog();
        evaluationLogStacking.setClassifierName(StackingClassifier.class.getSimpleName());
        evaluationLogStacking.setClassifierOptions(toJsonString(TestHelperUtils.createStackingOptions()));
        evaluationLogStacking.setCreationDate(CREATION_DATE);
        evaluationLogStacking.setRequestStatus(RequestStatus.FINISHED);
        instancesInfoRepository.save(evaluationLogStacking.getInstancesInfo());
        evaluationLogRepository.save(evaluationLogStacking);

        String searchQuery = evaluationLog.getRelationName().substring(0, 2);
        PageRequestDto pageRequestDto = new PageRequestDto(PAGE_NUMBER, PAGE_SIZE,
                Collections.singletonList(new SortFieldRequestDto(EvaluationLog_.CREATION_DATE, false)), searchQuery,
                newArrayList());
        pageRequestDto.getFilters().add(
                new FilterRequestDto(EvaluationLog_.CREATION_DATE, DATE_RANGE_VALUES, MatchMode.RANGE));
        pageRequestDto.getFilters().add(
                new FilterRequestDto(EvaluationLog_.RELATION_NAME,
                        Collections.singletonList(evaluationLog.getRelationName()), MatchMode.LIKE));
        pageRequestDto.getFilters().add(new FilterRequestDto(EvaluationLog_.REQUEST_STATUS,
                Collections.singletonList(RequestStatus.FINISHED.name()), MatchMode.EQUALS));
        var baseReportBean = evaluationLogsBaseReportDataFetcher.fetchReportData(pageRequestDto);
        assertThat(baseReportBean).isNotNull();
        assertThat(baseReportBean.getPage()).isOne();
        assertThat(baseReportBean.getTotalPages()).isOne();
        assertThat(baseReportBean.getSearchQuery()).isNotNull();
        assertThat(baseReportBean.getItems()).isNotNull();
        assertThat(baseReportBean.getItems()).hasSize(2);

        testGenerateReport(baseReportBean);
    }

    private void testGenerateReport(BaseReportBean<EvaluationLogBean> baseReportBean) throws IOException {
        String fileName = String.format("%s/target/evaluation-logs-report.xlsx", USER_DIR);
        @Cleanup var outputStream = new FileOutputStream(fileName);
        generateReport(EVALUATION_LOGS_REPORT_TEMPLATE_XLSX, baseReportBean, outputStream);
    }

    private void mockClassifiersFormTemplates() {
        FormTemplateGroupDto templates = loadClassifiersTemplates();
        FormTemplateGroupDto ensembleTemplates = loadEnsembleClassifiersTemplates();
        when(formTemplateProvider.getFormGroupDto(CLASSIFIERS)).thenReturn(templates);
        when(formTemplateProvider.getFormGroupDto(ENSEMBLE_CLASSIFIERS)).thenReturn(ensembleTemplates);
    }
}
