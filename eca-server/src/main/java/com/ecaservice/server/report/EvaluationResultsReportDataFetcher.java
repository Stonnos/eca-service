package com.ecaservice.server.report;

import com.ecaservice.server.report.model.EvaluationResultsReportBean;

/**
 * Evaluation results report data fetcher.
 *
 * @author Roman Batygin
 */
public interface EvaluationResultsReportDataFetcher {

    /**
     * Gets evaluation results report data.
     *
     * @param id - evaluation id
     * @return evaluation results report data
     */
    EvaluationResultsReportBean getReportData(long id);
}
