package com.ecaservice.data.storage.report;

import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.model.report.ReportType;
import com.ecaservice.data.storage.service.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import weka.core.Instances;

import javax.inject.Inject;

import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link InstancesReportService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({InstancesReportService.class, ReportProvider.class, EcaDsConfig.class})
class InstancesReportServiceTest {

    private static final long ID = 1L;
    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";

    @MockBean
    private StorageService storageService;

    @Inject
    private InstancesReportService instancesReportService;

    private Instances instances;

    @BeforeEach
    void init() {
        instances = loadInstances();
    }

    @Test
    void testGenerateReport() throws Exception {
        for (var reportType : ReportType.values()) {
            var httpServletResponse = new MockHttpServletResponse();
            when(storageService.getInstances(ID)).thenReturn(instances);
            instancesReportService.generateInstancesReport(ID, reportType, httpServletResponse);
            assertThat(httpServletResponse.getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            String expectedFileName = String.format("%s.%s", instances.relationName(), reportType.getExtension());
            String expectedContentDisposition = String.format(ATTACHMENT_FORMAT, expectedFileName);
            assertThat(httpServletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION)).isEqualTo(
                    expectedContentDisposition);
        }
    }
}
