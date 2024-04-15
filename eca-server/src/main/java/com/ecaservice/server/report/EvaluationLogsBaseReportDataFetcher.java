package com.ecaservice.server.report;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.report.data.fetcher.AbstractBaseReportDataFetcher;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.report.customize.InstancesInfoFilterReportCustomizer;
import com.ecaservice.server.report.model.BaseReportType;
import com.ecaservice.server.report.model.EvaluationLogBean;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.evaluation.EvaluationLogDataService;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.server.service.filter.dictionary.FilterDictionaries.CLASSIFIER_NAME;

/**
 * Evaluation logs data fetcher for base report.
 *
 * @author Roman Batygin
 */
@Component
public class EvaluationLogsBaseReportDataFetcher extends
        AbstractBaseReportDataFetcher<EvaluationLog, EvaluationLogBean> {

    private final EvaluationLogDataService evaluationLogDataService;
    private final EvaluationLogMapper evaluationLogMapper;
    private final InstancesInfoRepository instancesInfoRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterService            - filter template service bean
     * @param instancesInfoRepository  - instances info repository
     * @param evaluationLogDataService - evaluation log service bean
     * @param evaluationLogMapper      - evaluation log mapper bean
     */
    @Autowired
    public EvaluationLogsBaseReportDataFetcher(FilterTemplateService filterService,
                                               InstancesInfoRepository instancesInfoRepository,
                                               EvaluationLogDataService evaluationLogDataService,
                                               EvaluationLogMapper evaluationLogMapper) {
        super(BaseReportType.EVALUATION_LOGS.name(), FilterTemplateType.EVALUATION_LOG, filterService);
        this.evaluationLogDataService = evaluationLogDataService;
        this.evaluationLogMapper = evaluationLogMapper;
        this.instancesInfoRepository = instancesInfoRepository;
    }

    /**
     * Initialize report fetcher.
     */
    @PostConstruct
    public void init() {
        addFilterValueReportCustomizer(new InstancesInfoFilterReportCustomizer(instancesInfoRepository));
    }

    @Override
    protected Page<EvaluationLog> getItemsPage(PageRequestDto pageRequestDto) {
        return evaluationLogDataService.getNextPage(pageRequestDto);
    }

    @Override
    protected List<EvaluationLogBean> convertToBeans(Page<EvaluationLog> page) {
        return page.getContent()
                .stream()
                .map(evaluationLog -> {
                    var evaluationLogBean = evaluationLogMapper.mapToBean(evaluationLog);
                    var classifierName = getDictionaryLabelByCode(CLASSIFIER_NAME,
                            evaluationLog.getClassifierInfo().getClassifierName());
                    evaluationLogBean.setClassifierName(classifierName);
                    return evaluationLogBean;
                })
                .collect(Collectors.toList());
    }
}
