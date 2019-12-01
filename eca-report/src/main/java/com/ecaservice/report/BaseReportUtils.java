package com.ecaservice.report;

import com.ecaservice.report.model.BaseReportBean;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Base report utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class BaseReportUtils {

    private static final String REPORT_VARIABLE = "report";

    public static <T> void generateReport(String template, BaseReportBean<T> baseReportBean) throws IOException {
        @Cleanup InputStream is = BaseReportUtils.class.getClassLoader().getResourceAsStream(template);
        @Cleanup OutputStream os = new FileOutputStream("D:/" + template);
        Context context = new Context();
        context.putVar(REPORT_VARIABLE, baseReportBean);
        JxlsHelper.getInstance().processTemplate(is, os, context);
    }
}
