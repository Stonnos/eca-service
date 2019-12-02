package com.ecaservice.report;

import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.EvaluationLogBean;
import com.ecaservice.report.model.ExperimentBean;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    /**
     * Context variables
     */
    private static final String REPORT_VARIABLE = "report";

    /**
     * Generates experiments base report.
     *
     * @param beanBaseReportBean - experiments data for report
     * @param outputStream       - output stream
     * @throws IOException in case of I/O errors
     */
    public static void generateExperimentsReport(BaseReportBean<ExperimentBean> beanBaseReportBean,
                                                 OutputStream outputStream) throws IOException {
        generateReport(EXPERIMENTS_REPORT_TEMPLATE, beanBaseReportBean, outputStream);
    }

    /**
     * Generates evaluation logs base report.
     *
     * @param beanBaseReportBean - evaluation logs data for report
     * @param outputStream       - output stream
     * @throws IOException in case of I/O errors
     */
    public static void generateEvaluationLogsReport(BaseReportBean<EvaluationLogBean> beanBaseReportBean,
                                                    OutputStream outputStream) throws IOException {
        generateReport(EVALUATION_LOGS_REPORT_TEMPLATE, beanBaseReportBean, outputStream);
    }

    private static <T> void generateReport(String template,
                                           BaseReportBean<T> baseReportBean,
                                           OutputStream outputStream) throws IOException {
        @Cleanup InputStream inputStream = BaseReportGenerator.class.getClassLoader().getResourceAsStream(template);
        Context context = buildContext(baseReportBean);
        JxlsHelper.getInstance().processTemplate(inputStream, outputStream, context);
    }

    private static <T> Context buildContext(BaseReportBean<T> baseReportBean) {
        Context context = new Context();
        context.putVar(REPORT_VARIABLE, baseReportBean);
        return context;
    }
}
