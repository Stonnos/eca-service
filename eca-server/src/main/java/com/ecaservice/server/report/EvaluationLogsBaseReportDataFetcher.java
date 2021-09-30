package com.ecaservice.server.report;

import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.report.data.fetcher.AbstractBaseReportDataFetcher;
import com.ecaservice.report.model.EvaluationLogBean;
import com.ecaservice.report.model.ReportType;
import com.ecaservice.server.service.evaluation.EvaluationLogService;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

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

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterService        - filter service bean
     * @param evaluationLogService - evaluation log service bean
     * @param evaluationLogMapper  - evaluation log mapper bean
     */
    @Inject
    public EvaluationLogsBaseReportDataFetcher(FilterService filterService,
                                               EvaluationLogService evaluationLogService,
                                               EvaluationLogMapper evaluationLogMapper) {
        super(ReportType.EVALUATION_LOGS, EvaluationLog.class, FilterTemplateType.EVALUATION_LOG.name(), filterService);
        this.evaluationLogService = evaluationLogService;
        this.evaluationLogMapper = evaluationLogMapper;
    }

    @Override
    protected Page<EvaluationLog> getItemsPage(PageRequestDto pageRequestDto) {
        return evaluationLogService.getNextPage(pageRequestDto);
    }

    @Override
    protected List<EvaluationLogBean> convertToBeans(Page<EvaluationLog> page) {
        return evaluationLogMapper.mapToBeans(page.getContent());
    }
}
