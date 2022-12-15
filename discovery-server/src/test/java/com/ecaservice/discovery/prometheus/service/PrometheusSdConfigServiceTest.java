package com.ecaservice.discovery.prometheus.service;

import com.ecaservice.discovery.model.MetricsInstanceInfo;
import com.ecaservice.discovery.service.MetricsDiscoveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static com.ecaservice.discovery.TestHelperUtils.createMetricsInstanceInfo;
import static com.ecaservice.discovery.prometheus.Labels.APP_INSTANCE_LABEL_NAME;
import static com.ecaservice.discovery.prometheus.Labels.APP_NAME_LABEL_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PrometheusSdConfigService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class PrometheusSdConfigServiceTest {

    @Mock
    private MetricsDiscoveryService metricsDiscoveryService;
    @InjectMocks
    private PrometheusSdConfigService prometheusSdConfigService;

    private MetricsInstanceInfo metricsInstanceInfo;

    @BeforeEach
    void init() {
        metricsInstanceInfo = createMetricsInstanceInfo();
        when(metricsDiscoveryService.getInstances()).thenReturn(Collections.singletonList(metricsInstanceInfo));
    }

    @Test
    void testGetSdConfigs() {
        var sdConfigs = prometheusSdConfigService.getSdConfigs();
        assertThat(sdConfigs).hasSize(1);
        var sdConfig = sdConfigs.iterator().next();
        assertThat(sdConfig.getTargets()).hasSize(1);
        String expectedTarget =
                String.format("%s:%d", metricsInstanceInfo.getHostName(), metricsInstanceInfo.getManagementPort());
        assertThat(sdConfig.getTargets()).containsOnly(expectedTarget);
        assertThat(sdConfig.getLabels())
                .containsEntry(APP_NAME_LABEL_NAME, metricsInstanceInfo.getAppName())
                .containsEntry(APP_INSTANCE_LABEL_NAME, String.format("%s-%d", metricsInstanceInfo.getAppName(),
                        metricsInstanceInfo.getInstanceNumber()));
    }
}
