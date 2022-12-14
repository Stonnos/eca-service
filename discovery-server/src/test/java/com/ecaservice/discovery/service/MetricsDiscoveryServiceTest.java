package com.ecaservice.discovery.service;

import com.ecaservice.discovery.model.MetricsInstanceStatus;
import com.netflix.appinfo.InstanceInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MetricsDiscoveryService} class.
 *
 * @author Roman Batygin
 */
class MetricsDiscoveryServiceTest {

    private static final String MANAGEMENT_PORT_KEY = "management.port";
    private static final int MANAGEMENT_PORT = 8080;
    private static final String IP_ADDRESS = "192.168.19.11";
    private static final String APP_NAME = "eca-server";

    private MetricsDiscoveryService metricsDiscoveryService;

    @BeforeEach
    void init() {
        metricsDiscoveryService = new MetricsDiscoveryService();
    }

    @Test
    void testSaveNewInstance() {
        var instanceInfo = mockInstanceInfo(UUID.randomUUID().toString());
        metricsDiscoveryService.saveOrUpdateInstance(instanceInfo);
        verifyMetricsInstance(instanceInfo, MetricsInstanceStatus.UP);
    }

    @Test
    void testMarkInstanceDown() {
        var instanceInfo = mockInstanceInfo(UUID.randomUUID().toString());
        metricsDiscoveryService.saveOrUpdateInstance(instanceInfo);
        metricsDiscoveryService.markAsDown(instanceInfo.getId());
        verifyMetricsInstance(instanceInfo, MetricsInstanceStatus.DOWN);
    }

    @Test
    void testUpdateDownInstancesWithNewId() {
        var instanceInfo = mockInstanceInfo(UUID.randomUUID().toString());
        metricsDiscoveryService.saveOrUpdateInstance(instanceInfo);
        metricsDiscoveryService.markAsDown(instanceInfo.getId());
        instanceInfo = mockInstanceInfo(UUID.randomUUID().toString());
        metricsDiscoveryService.saveOrUpdateInstance(instanceInfo);
        verifyMetricsInstance(instanceInfo, MetricsInstanceStatus.UP);
    }

    private void verifyMetricsInstance(InstanceInfo instanceInfo, MetricsInstanceStatus expectedStatus) {
        var metricsInstanceInfo = metricsDiscoveryService.getInstances()
                .stream()
                .filter(instance -> instance.getInstanceId().equals(instanceInfo.getId()))
                .findFirst()
                .orElse(null);
        assertThat(metricsInstanceInfo).isNotNull();
        assertThat(metricsInstanceInfo.getAppName()).isEqualTo(instanceInfo.getAppName());
        assertThat(metricsInstanceInfo.getInstanceId()).isEqualTo(instanceInfo.getId());
        assertThat(metricsInstanceInfo.getHostName()).isEqualTo(instanceInfo.getHostName());
        assertThat(metricsInstanceInfo.getIpAddress()).isEqualTo(instanceInfo.getIPAddr());
        assertThat(metricsInstanceInfo.getLastSyncDate()).isNotNull();
        assertThat(metricsInstanceInfo.getStatus()).isEqualTo(expectedStatus);
        assertThat(metricsInstanceInfo.getManagementPort()).isEqualTo(MANAGEMENT_PORT);
        assertThat(metricsInstanceInfo.getInstanceNumber()).isOne();
    }

    private InstanceInfo mockInstanceInfo(String instanceId) {
        var instanceInfo = mock(InstanceInfo.class);
        when(instanceInfo.getAppName()).thenReturn(APP_NAME);
        when(instanceInfo.getIPAddr()).thenReturn(IP_ADDRESS);
        when(instanceInfo.getId()).thenReturn(instanceId);
        when(instanceInfo.getHostName()).thenReturn(instanceId);
        when(instanceInfo.getMetadata()).thenReturn(
                Collections.singletonMap(MANAGEMENT_PORT_KEY, String.valueOf(MANAGEMENT_PORT)));
        return instanceInfo;
    }
}
