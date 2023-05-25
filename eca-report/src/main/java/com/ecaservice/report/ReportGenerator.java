package com.ecaservice.report;

import com.ecaservice.report.exception.ReportException;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

/**
 * Class for reports generation.
 *
 * @author Roman Batygin
 */
@Slf4j
@UtilityClass
public class ReportGenerator {

    /**
     * Templates paths
     */
    private static final String REPORTS_DIRECTORY = "report-templates";

    /**
     * Context variables
     */
    private static final String REPORT_VARIABLE = "report";

    /**
     * Generates report.
     *
     * @param template     - report template
     * @param reportBean   - report bean
     * @param outputStream - output stream object
     */
    public static void generateReport(String template, Object reportBean, OutputStream outputStream) {
        log.info("Starting to generate xlsx report template [{}]", template);
        try {
            String templatePath = String.format("%s/%s", REPORTS_DIRECTORY, template);
            @Cleanup InputStream inputStream = ReportGenerator.class.getClassLoader().getResourceAsStream(templatePath);
            Context context = new Context(Collections.singletonMap(REPORT_VARIABLE, reportBean));
            JxlsHelper.getInstance().processTemplate(inputStream, outputStream, context);
            log.info("Xlsx report [{}] has been processed", template);
        } catch (IOException ex) {
            throw new ReportException(ex.getMessage());
        }
    }
}
