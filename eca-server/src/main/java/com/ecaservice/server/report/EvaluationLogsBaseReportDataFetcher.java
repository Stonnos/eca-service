package com.ecaservice.server.report;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.report.data.fetcher.AbstractBaseReportDataFetcher;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.report.customize.InstancesInfoFilterReportCustomizer;
import com.ecaservice.server.report.model.BaseReportType;
import com.ecaservice.server.report.model.EvaluationLogBean;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.evaluation.EvaluationLogService;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
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

    private final EvaluationLogService evaluationLogService;
    private final EvaluationLogMapper evaluationLogMapper;
    private final InstancesInfoRepository instancesInfoRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterService           - filter service bean
     * @param instancesInfoRepository - instances info repository
     * @param evaluationLogService    - evaluation log service bean
     * @param evaluationLogMapper     - evaluation log mapper bean
     */
    @Inject
    public EvaluationLogsBaseReportDataFetcher(FilterService filterService,
                                               InstancesInfoRepository instancesInfoRepository,
                                               EvaluationLogService evaluationLogService,
                                               EvaluationLogMapper evaluationLogMapper) {
        super(BaseReportType.EVALUATION_LOGS.name(), FilterTemplateType.EVALUATION_LOG, filterService);
        this.evaluationLogService = evaluationLogService;
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
        return evaluationLogService.getNextPage(pageRequestDto);
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
