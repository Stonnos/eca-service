package com.ecaservice.util;

import com.ecaservice.report.model.ReportType;
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

    private static final String FILE_NAME_FORMAT = "%s.xlsx";
    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";

    /**
     * Downloads report into http servlet response output stream.
     *
     * @param reportType          - report type
     * @param httpServletResponse - http servlet response output stream
     * @param fileName            - file name
     * @param data                - report data
     * @throws IOException in case of I/O error
     */
    public static void download(ReportType reportType, String fileName, HttpServletResponse httpServletResponse,
                                Object data) throws IOException {
        String targetFile = String.format(FILE_NAME_FORMAT, fileName);
        @Cleanup OutputStream outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, targetFile));
        generateReport(reportType, data, outputStream);
        outputStream.flush();
    }
}
