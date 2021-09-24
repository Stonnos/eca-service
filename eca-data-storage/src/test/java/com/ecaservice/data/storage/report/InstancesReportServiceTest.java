package com.ecaservice.data.storage.report;

import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.model.MockHttpServletResponseResource;
import com.ecaservice.data.storage.model.report.ReportType;
import com.ecaservice.data.storage.service.StorageService;
import eca.data.file.FileDataLoader;
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
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.stream.IntStream;

import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
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
            assertResponse(expectedFileName, httpServletResponse);
        }
    }

    private void assertResponse(String expectedFileName, MockHttpServletResponse httpServletResponse) throws Exception {
        FileDataLoader fileDataLoader = new FileDataLoader();
        fileDataLoader.setSource(new MockHttpServletResponseResource(expectedFileName, httpServletResponse));
        Instances actual = fileDataLoader.loadInstances();
        assertInstances(instances, actual);
    }

    private void assertInstances(Instances expected, Instances actual) {
        assertThat(actual).isNotNull();
        assertThat(actual.numAttributes()).isEqualTo(expected.numAttributes());
        assertThat(actual.numInstances()).isEqualTo(expected.numInstances());
        IntStream.range(0, expected.numInstances()).forEach(i -> {
            Instance expectedInstance = expected.instance(i);
            Instance actualInstance = actual.instance(i);
            IntStream.range(0, expectedInstance.numAttributes()).forEach(j -> {
                Attribute attribute = expectedInstance.attribute(j);
                switch (attribute.type()) {
                    case Attribute.NOMINAL:
                    case Attribute.DATE:
                        assertThat(actualInstance.stringValue(j)).isEqualTo(expectedInstance.stringValue(j));
                        break;
                    case Attribute.NUMERIC:
                        assertThat(actualInstance.value(j)).isEqualTo(expectedInstance.value(j));
                        break;
                    default:
                        fail("Expected numeric or nominal attribute type!");
                }
            });
        });
    }
}
