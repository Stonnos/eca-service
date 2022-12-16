package com.ecaservice.discovery;

import com.ecaservice.discovery.model.MetricsInstanceInfo;
import com.ecaservice.discovery.model.MetricsInstanceStatus;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final int MANAGEMENT_PORT = 8080;
    private static final String ECA_SERVER = "eca-server";
    private static final String IP_ADDRESS = "192.168.0.1";

    /**
     * Creates metrics instance info.
     *
     * @return metrics instance info
     */
    public static MetricsInstanceInfo createMetricsInstanceInfo() {
        MetricsInstanceInfo metricsInstanceInfo = new MetricsInstanceInfo();
        metricsInstanceInfo.setAppName(ECA_SERVER);
        metricsInstanceInfo.setInstanceId(UUID.randomUUID().toString());
        metricsInstanceInfo.setIpAddress(IP_ADDRESS);
        metricsInstanceInfo.setHostName(UUID.randomUUID().toString());
        metricsInstanceInfo.setLastUpdatedDate(LocalDateTime.now());
        metricsInstanceInfo.setInstanceNumber(0);
        metricsInstanceInfo.setManagementPort(MANAGEMENT_PORT);
        metricsInstanceInfo.setStatus(MetricsInstanceStatus.UP);
        return metricsInstanceInfo;
    }
}
