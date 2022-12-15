package com.ecaservice.discovery.prometheus.service;

import com.ecaservice.discovery.model.MetricsInstanceInfo;
import com.ecaservice.discovery.prometheus.dto.PrometheusSdConfig;
import com.ecaservice.discovery.service.MetricsDiscoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ecaservice.discovery.prometheus.Labels.APP_INSTANCE_LABEL_NAME;
import static com.ecaservice.discovery.prometheus.Labels.APP_INSTANCE_METRICS_PATH_LABEL_NAME;
import static com.ecaservice.discovery.prometheus.Labels.APP_NAME_LABEL_NAME;

/**
 * Prometheus service discovery config service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrometheusSdConfigService {

    private static final String ACTUATOR_PROMETHEUS_PATH = "/actuator/prometheus";

    private final MetricsDiscoveryService metricsDiscoveryService;

    /**
     * Gets service discovery configs for prometheus.
     *
     * @return sd configs list
     */
    public List<PrometheusSdConfig> getSdConfigs() {
        log.info("Gets service discovery configs for prometheus");
        var metricInstances = metricsDiscoveryService.getInstances();
        var prometheusSdConfigs = metricInstances
                .stream()
                .map(this::createPrometheusSdConfig)
                .collect(Collectors.toList());
        log.debug("Fetched service discovery configs for prometheus: {}", prometheusSdConfigs);
        log.info("[{}] service discovery configs has been fetched for prometheus", prometheusSdConfigs.size());
        return prometheusSdConfigs;
    }

    private PrometheusSdConfig createPrometheusSdConfig(MetricsInstanceInfo metricsInstanceInfo) {
        var prometheusSdConfig = new PrometheusSdConfig();
        String target =
                String.format("%s:%d", metricsInstanceInfo.getHostName(), metricsInstanceInfo.getManagementPort());
        Map<String, String> labels = createLabels(metricsInstanceInfo);
        prometheusSdConfig.setTargets(Collections.singletonList(target));
        prometheusSdConfig.setLabels(labels);
        return prometheusSdConfig;
    }

    private Map<String, String> createLabels(MetricsInstanceInfo metricsInstanceInfo) {
        String appName = metricsInstanceInfo.getAppName().toLowerCase();
        String instance = String.format("%s-%d", appName, metricsInstanceInfo.getInstanceNumber());
        return Map.of(
                APP_NAME_LABEL_NAME, appName,
                APP_INSTANCE_LABEL_NAME, instance,
                APP_INSTANCE_METRICS_PATH_LABEL_NAME, ACTUATOR_PROMETHEUS_PATH
        );
    }
}
