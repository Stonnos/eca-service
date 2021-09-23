package com.ecaservice.data.storage.report;

import com.ecaservice.data.storage.config.EcaDsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ReportsConfigurationService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import(EcaDsConfig.class)
class ReportsConfigurationServiceTest {

    private static final int EXPECTED_REPORTS_SIZE = 8;

    @Inject
    private EcaDsConfig ecaDsConfig;

    private ReportsConfigurationService reportsConfigurationService;

    @BeforeEach
    void init() {
        reportsConfigurationService = new ReportsConfigurationService(ecaDsConfig);
    }

    @Test
    void testInitializeReportsConfiguration() throws IOException {
        reportsConfigurationService.initializeReportsProperties();
        assertThat(reportsConfigurationService.getReportProperties()).hasSize(EXPECTED_REPORTS_SIZE);
    }
}
