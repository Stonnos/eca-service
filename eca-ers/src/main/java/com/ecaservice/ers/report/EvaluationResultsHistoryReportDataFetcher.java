package com.ecaservice.ers.report;

import com.ecaservice.classifier.template.processor.service.ClassifierOptionsProcessor;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.ers.config.ErsConfig;
import com.ecaservice.ers.mapping.EvaluationResultsMapper;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.report.customize.InstancesInfoFilterReportCustomizer;
import com.ecaservice.ers.report.model.EvaluationResultsHistoryBean;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import com.ecaservice.ers.service.EvaluationResultsHistoryService;
import com.ecaservice.report.data.fetcher.AbstractBaseReportDataFetcher;
import com.ecaservice.web.dto.model.PageRequestDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.classifier.template.processor.util.Utils.getClassifierOptionsDetailsString;
import static com.ecaservice.classifier.template.processor.util.Utils.parseOptions;
import static com.ecaservice.ers.dictionary.FilterDictionaries.CLASSIFIER_NAME;
import static com.ecaservice.ers.dictionary.FilterDictionaries.EVALUATION_RESULTS_HISTORY_TEMPLATE;
import static com.ecaservice.ers.util.RoutePaths.EVALUATION_RESULTS_REQUEST_PATH;

/**
 * Evaluation results history data fetcher for base report.
 *
 * @author Roman Batygin
 */
@Component
public class EvaluationResultsHistoryReportDataFetcher
        extends AbstractBaseReportDataFetcher<EvaluationResultsInfo, EvaluationResultsHistoryBean> {

    private final ErsConfig ersConfig;
    private final EvaluationResultsHistoryService evaluationResultsHistoryService;
    private final EvaluationResultsMapper evaluationResultsMapper;
    private final ClassifierOptionsProcessor classifierOptionsProcessor;
    private final InstancesInfoRepository instancesInfoRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param ersConfig                       - ers config
     * @param filterTemplateService           - filter service bean
     * @param evaluationResultsHistoryService - evaluation results history service bean
     * @param evaluationResultsMapper         - evaluation results mapper
     * @param classifierOptionsProcessor      - classifier options processor
     * @param instancesInfoRepository         - instances info repository
     */
    @Autowired
    public EvaluationResultsHistoryReportDataFetcher(ErsConfig ersConfig,
                                                     FilterTemplateService filterTemplateService,
                                                     EvaluationResultsHistoryService evaluationResultsHistoryService,
                                                     EvaluationResultsMapper evaluationResultsMapper,
                                                     ClassifierOptionsProcessor classifierOptionsProcessor,
                                                     InstancesInfoRepository instancesInfoRepository) {
        super("EVALUATION_RESULTS_HISTORY", EVALUATION_RESULTS_HISTORY_TEMPLATE, ersConfig.getMaxPagesNum(),
                filterTemplateService);
        this.ersConfig = ersConfig;
        this.evaluationResultsHistoryService = evaluationResultsHistoryService;
        this.evaluationResultsMapper = evaluationResultsMapper;
        this.classifierOptionsProcessor = classifierOptionsProcessor;
        this.instancesInfoRepository = instancesInfoRepository;
    }

    @PostConstruct
    public void init() {
        addFilterValueReportCustomizer(new InstancesInfoFilterReportCustomizer(instancesInfoRepository));
    }

    @Override
    protected Page<EvaluationResultsInfo> getItemsPage(PageRequestDto pageRequestDto) {
        return evaluationResultsHistoryService.getEvaluationResultsInfoPage(pageRequestDto);
    }

    @Override
    protected List<EvaluationResultsHistoryBean> convertToBeans(Page<EvaluationResultsInfo> page) {
        return page.getContent()
                .stream()
                .map(this::convertToEvaluationResultsHistoryBean)
                .collect(Collectors.toList());
    }

    private EvaluationResultsHistoryBean convertToEvaluationResultsHistoryBean(
            EvaluationResultsInfo evaluationResultsInfo) {
        var evaluationResultsHistoryBean =
                evaluationResultsMapper.mapToEvaluationResultsHistoryBean(evaluationResultsInfo);
        String classifierName =
                getDictionaryLabelByCode(CLASSIFIER_NAME, evaluationResultsInfo.getClassifierName());
        String requestPathUrl = getRequestPathUrl(evaluationResultsInfo);
        var classifierOptions =
                parseOptions(evaluationResultsInfo.getClassifierOptions());
        var classifierInfoDto =
                classifierOptionsProcessor.processClassifierOptions(classifierOptions);
        evaluationResultsHistoryBean.setClassifierName(classifierName);
        evaluationResultsHistoryBean.setClassifierOptions(
                getClassifierOptionsDetailsString(classifierInfoDto));
        evaluationResultsHistoryBean.setRequestPathUrl(requestPathUrl);
        return evaluationResultsHistoryBean;
    }

    private String getRequestPathUrl(EvaluationResultsInfo evaluationResultsInfo) {
        String path = String.format(EVALUATION_RESULTS_REQUEST_PATH, evaluationResultsInfo.getRequestId());
        return String.format("%s%s", ersConfig.getWebExternalBaseUrl(), path);
    }
}
