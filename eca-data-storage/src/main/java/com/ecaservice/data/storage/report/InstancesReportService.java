package com.ecaservice.data.storage.report;

import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.InstancesReportException;
import com.ecaservice.data.storage.model.report.ReportType;
import com.ecaservice.data.storage.service.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.model.InstancesModel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

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
    private final ObjectMapper objectMapper;

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
        log.info("Starting to generate report [{}] for instances with id [{}]", reportType, instancesEntity.getId());
        var instances = storageService.getInstances(instancesEntity);
        String reportName = String.format("%s.%s", instances.relationName(), reportType.getExtension());
        generate(httpServletResponse, reportName,
                outputStream -> generateInstancesReport(instances, reportType, outputStream));
        log.info("Report [{}] has been generated for instances with id [{}]", reportType, instancesEntity.getId());
    }

    /**
     * Generates instances report with selected attributes in json format.
     *
     * @param instancesEntity     - instances entity
     * @param httpServletResponse - http servlet response
     * @throws Exception in case of error
     */
    public void generateJsonInstancesReportWithSelectedAttributes(InstancesEntity instancesEntity,
                                                                  HttpServletResponse httpServletResponse)
            throws Exception {
        log.info("Starting to generate report with selected attributes for instances with id [{}]",
                instancesEntity.getId());
        var instancesModel = storageService.getInstancesModelWithSelectedAttributes(instancesEntity);
        String reportName = String.format("%s.json", instancesEntity.getTableName());
        generate(httpServletResponse, reportName,
                outputStream -> generateJsonInstancesReport(instancesModel, outputStream));
        log.info("Report with selected attributes has been generated for instances with id [{}]",
                instancesEntity.getId());
    }

    private void generateInstancesReport(Instances instances, ReportType reportType, OutputStream outputStream) {
        try {
            var reportSaver = reportType.handle(reportProvider);
            reportSaver.setDateFormat(ecaDsConfig.getDateFormat());
            reportSaver.write(instances, outputStream);
        } catch (Exception ex) {
            throw new InstancesReportException(ex);
        }
    }

    private void generateJsonInstancesReport(InstancesModel instanceModel, OutputStream outputStream) {
        try {
            objectMapper.writeValue(outputStream, instanceModel);
        } catch (Exception ex) {
            throw new InstancesReportException(ex);
        }
    }

    private void generate(HttpServletResponse httpServletResponse,
                          String reportName,
                          Consumer<OutputStream> consumer) throws IOException {
        @Cleanup var outputStream = httpServletResponse.getOutputStream();
        httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format(ATTACHMENT_FORMAT, reportName));
        consumer.accept(outputStream);
        outputStream.flush();
    }
}
