package com.ecaservice.server.report;

import com.ecaservice.server.report.model.BaseReportType;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Report templates.
 *
 * @author Roman Batygin
 */
@Slf4j
@UtilityClass
public class ReportTemplates {

    /**
     * Classifiers configuration template
     */
    public static final String CLASSIFIERS_CONFIGURATION_TEMPLATE = "classifiers-configuration-report-template.xlsx";

    /**
     * Base report templates
     */
    public static final Map<BaseReportType, String> BASE_REPORT_TEMPLATES = Map.of(
            BaseReportType.EXPERIMENTS, "/experiments-report-template.xlsx",
            BaseReportType.EVALUATION_LOGS, "evaluation-logs-report-template.xlsx",
            BaseReportType.CLASSIFIERS_OPTIONS_REQUESTS, "classifier-options-requests-report-template.xlsx"
    );
}
