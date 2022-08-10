package com.ecaservice.data.storage.report;

import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.model.report.ReportType;
import com.ecaservice.data.storage.service.StorageService;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

import static com.ecaservice.data.storage.config.audit.AuditCodes.DOWNLOAD_INSTANCES_REPORT;

/**
 * Instances report service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesReportService {

    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";

    private final EcaDsConfig ecaDsConfig;
    private final StorageService storageService;
    private final ReportProvider reportProvider;

    /**
     * Generates instances report with specified type.
     *
     * @param instancesEntity     - instances entity
     * @param reportType          - report type
     * @param httpServletResponse - http servlet response
     * @throws Exception in case of error
     */
    @Audit(value = DOWNLOAD_INSTANCES_REPORT, correlationIdKey = "#instancesEntity.id")
    public void generateInstancesReport(InstancesEntity instancesEntity,
                                        ReportType reportType,
                                        HttpServletResponse httpServletResponse) throws Exception {
        log.info("Starting to generate report [{}] for instances with id [{}]", reportType, instancesEntity);
        var instances = storageService.getInstances(instancesEntity);
        @Cleanup var outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String reportName = String.format("%s.%s", instances.relationName(), reportType.getExtension());
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, reportName));
        generateInstancesReport(instances, reportType, outputStream);
        outputStream.flush();
        log.info("Report [{}] has been generated for instances with id [{}]", reportType, instancesEntity);
    }

    private void generateInstancesReport(Instances instances, ReportType reportType,
                                         OutputStream outputStream) throws Exception {
        var reportSaver = reportType.handle(reportProvider);
        reportSaver.setDateFormat(ecaDsConfig.getDateFormat());
        reportSaver.write(instances, outputStream);
    }
}
