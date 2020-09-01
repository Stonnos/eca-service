package com.ecaservice.report;

import com.ecaservice.report.exception.ReportException;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.ReportType;
import com.ecaservice.report.model.ReportTypeVisitor;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

/**
 * Class for base reports generation.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class BaseReportGenerator {

    /**
     * Templates paths
     */
    private static final String REPORTS_DIRECTORY = "templates";

    private static final String EXPERIMENTS_REPORT_TEMPLATE = REPORTS_DIRECTORY + "/experiments-report-template.xlsx";
    private static final String EVALUATION_LOGS_REPORT_TEMPLATE =
            REPORTS_DIRECTORY + "/evaluation-logs-report-template.xlsx";
    private static final String CLASSIFIER_OPTIONS_REQUESTS_TEMPLATE =
            REPORTS_DIRECTORY + "/classifier-options-requests-report-template.xlsx";

    /**
     * Context variables
     */
    private static final String REPORT_VARIABLE = "report";


    /**
     * Generates report.
     *
     * @param reportType     - report type
     * @param baseReportBean - report bean
     * @param outputStream   - output stream object
     * @param <T>            - item generic type
     */
    public static <T> void generateReport(ReportType reportType, BaseReportBean<T> baseReportBean,
                                          OutputStream outputStream) {
        reportType.handle(new ReportTypeVisitor() {
            @Override
            public void caseExperiments() {
                generateReport(EXPERIMENTS_REPORT_TEMPLATE, baseReportBean, outputStream);
            }

            @Override
            public void caseEvaluationLogs() {
                generateReport(EVALUATION_LOGS_REPORT_TEMPLATE, baseReportBean, outputStream);
            }

            @Override
            public void caseClassifierOptionsRequests() {
                generateReport(CLASSIFIER_OPTIONS_REQUESTS_TEMPLATE, baseReportBean, outputStream);
            }
        });
    }

    private static <T> void generateReport(String template, BaseReportBean<T> baseReportBean,
                                           OutputStream outputStream) {
        try {
            @Cleanup InputStream inputStream = BaseReportGenerator.class.getClassLoader().getResourceAsStream(template);
            Context context = new Context(Collections.singletonMap(REPORT_VARIABLE, baseReportBean));
            JxlsHelper.getInstance().processTemplate(inputStream, outputStream, context);
        } catch (IOException ex) {
            throw new ReportException(ex.getMessage());
        }
    }
}
