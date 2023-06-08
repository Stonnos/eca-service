package com.ecaservice.server.util;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static com.ecaservice.report.ReportGenerator.generateReport;

/**
 * Report helper class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ReportHelper {

    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";

    /**
     * Downloads report into http servlet response output stream.
     *
     * @param template            - report template
     * @param httpServletResponse - http servlet response output stream
     * @param fileName            - file name
     * @param data                - report data
     * @throws IOException in case of I/O error
     */
    public static void download(String template, String fileName, HttpServletResponse httpServletResponse,
                                Object data) throws IOException {
        @Cleanup OutputStream outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, fileName));
        generateReport(template, data, outputStream);
        outputStream.flush();
    }
}
