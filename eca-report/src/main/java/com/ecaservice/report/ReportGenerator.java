package com.ecaservice.report;

import com.ecaservice.report.exception.ReportException;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiTransformer;
import org.jxls.util.JxlsHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

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
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            String templatePath = String.format("%s/%s", REPORTS_DIRECTORY, template);
            @Cleanup InputStream inputStream = ReportGenerator.class.getClassLoader().getResourceAsStream(templatePath);
            Context context = PoiTransformer.createInitialContext();
            context.putVar(REPORT_VARIABLE, reportBean);
            JxlsHelper.getInstance().processTemplate(inputStream, outputStream, context);
            stopWatch.stop();
            log.info("Xlsx report [{}] has been processed: {} ms.", template, stopWatch.getTime(TimeUnit.MILLISECONDS));
        } catch (IOException ex) {
            throw new ReportException(ex.getMessage());
        }
    }
}
