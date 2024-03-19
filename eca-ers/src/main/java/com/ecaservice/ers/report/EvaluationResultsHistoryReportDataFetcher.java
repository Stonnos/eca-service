package com.ecaservice.ers.report;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.ers.mapping.EvaluationResultsMapper;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.report.customize.InstancesInfoFilterReportCustomizer;
import com.ecaservice.ers.report.model.EvaluationResultsHistoryBean;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import com.ecaservice.ers.service.EvaluationResultsHistoryService;
import com.ecaservice.report.data.fetcher.AbstractBaseReportDataFetcher;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.ers.dictionary.FilterDictionaries.CLASSIFIER_NAME;
import static com.ecaservice.ers.dictionary.FilterDictionaries.EVALUATION_RESULTS_HISTORY_TEMPLATE;

/**
 * Evaluation results history data fetcher for base report.
 *
 * @author Roman Batygin
 */
@Component
public class EvaluationResultsHistoryReportDataFetcher
        extends AbstractBaseReportDataFetcher<EvaluationResultsInfo, EvaluationResultsHistoryBean> {

    private final EvaluationResultsHistoryService evaluationResultsHistoryService;
    private final EvaluationResultsMapper evaluationResultsMapper;
    private final InstancesInfoRepository instancesInfoRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterTemplateService           - filter service bean
     * @param evaluationResultsHistoryService - evaluation results history service bean
     * @param evaluationResultsMapper         - evaluation results mapper
     * @param instancesInfoRepository         - instances info repository
     */
    @Inject
    public EvaluationResultsHistoryReportDataFetcher(FilterTemplateService filterTemplateService,
                                                     EvaluationResultsHistoryService evaluationResultsHistoryService,
                                                     EvaluationResultsMapper evaluationResultsMapper,
                                                     InstancesInfoRepository instancesInfoRepository) {
        super("EVALUATION_RESULTS_HISTORY", EVALUATION_RESULTS_HISTORY_TEMPLATE, filterTemplateService);
        this.evaluationResultsHistoryService = evaluationResultsHistoryService;
        this.evaluationResultsMapper = evaluationResultsMapper;
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
                .map(evaluationResultsInfo -> {
                    var evaluationResultsHistoryBean =
                            evaluationResultsMapper.mapToEvaluationResultsHistoryBean(evaluationResultsInfo);
                    var classifierName = getDictionaryLabelByCode(CLASSIFIER_NAME,
                            evaluationResultsInfo.getClassifierInfo().getClassifierName());
                    evaluationResultsHistoryBean.setClassifierName(classifierName);
                    return evaluationResultsHistoryBean;
                })
                .collect(Collectors.toList());
    }
}
