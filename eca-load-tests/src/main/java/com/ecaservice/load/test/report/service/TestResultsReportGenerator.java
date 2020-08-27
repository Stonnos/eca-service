package com.ecaservice.load.test.report.service;

import com.ecaservice.load.test.report.bean.LoadTestBean;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

/**
 * Test results report generator.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class TestResultsReportGenerator {

    public static final String REPORT_VARIABLE = "report";
    /**
     * Templates paths
     */
    private static final String REPORTS_DIRECTORY = "templates";

    private static final String LOAD_TEST_REPORT_TEMPLATE = REPORTS_DIRECTORY + "/load-test-report-template.xlsx";

    private final JxlsHelper jxlsHelper;

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Constructor with spring dependency injection.
     *
     * @param jxlsHelper - jxls helper bean
     */
    public TestResultsReportGenerator(JxlsHelper jxlsHelper) {
        this.jxlsHelper = jxlsHelper;
    }

    /**
     * Generates load test results report.
     *
     * @param loadTestBean - load test bean
     * @param outputStream - output stream
     * @throws IOException in case of I/O error
     */
    public void generateReport(LoadTestBean loadTestBean, OutputStream outputStream) throws IOException {
        Resource resource = resolver.getResource(LOAD_TEST_REPORT_TEMPLATE);
        @Cleanup InputStream inputStream = resource.getInputStream();
        Context context = new Context(Collections.singletonMap(REPORT_VARIABLE, loadTestBean));
        jxlsHelper.processTemplate(inputStream, outputStream, context);
    }
}
