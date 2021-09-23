package com.ecaservice.data.storage.report;

import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.model.report.ReportType;
import com.ecaservice.data.storage.service.StorageService;
import eca.data.file.xls.XLSSaver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import weka.core.Instances;

import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link InstancesReportService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class InstancesReportServiceTest {

    private static final long ID = 1L;
    private static final String ATTACHMENT_FORMAT = "attachment; filename=%s";

    @Mock
    private StorageService storageService;
    @Mock
    private ReportProvider reportProvider;
    @Mock
    private EcaDsConfig ecaDsConfig;

    @InjectMocks
    private InstancesReportService instancesReportService;

    private Instances instances;

    @BeforeEach
    void init() {
        instances = loadInstances();
    }

    @Test
    void testGenerateReport() throws Exception {
        var xlsSaver = mock(XLSSaver.class);
        var httpServletResponse = new MockHttpServletResponse();
        when(storageService.getInstances(ID)).thenReturn(instances);
        when(reportProvider.visitXls()).thenReturn(xlsSaver);
        instancesReportService.generateInstancesReport(ID, ReportType.XLS, httpServletResponse);
        assertThat(httpServletResponse.getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String expectedFileName = String.format("%s.%s", instances.relationName(), ReportType.XLS.getExtension());
        String expectedContentDisposition = String.format(ATTACHMENT_FORMAT, expectedFileName);
        assertThat(httpServletResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION)).isEqualTo(
                expectedContentDisposition);
    }
}
