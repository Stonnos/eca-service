package com.ecaservice.discovery.service;

import com.ecaservice.discovery.model.MetricsInstanceInfo;
import com.ecaservice.discovery.model.MetricsInstanceStatus;
import com.netflix.appinfo.InstanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Metrics discovery service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class MetricsDiscoveryService {

    private static final String MANAGEMENT_PORT_KEY = "management.port";
    private static final String REWRITE_INSTANCE_LOG_MESSAGE =
            "First [{}] down instance with ip [{}] has been found for app name [{}]. " +
                    "Rewrite it with new id [{}], ip [{}]";

    private final Map<String, Integer> appCounters = newHashMap();
    private final List<MetricsInstanceInfo> instances = newArrayList();

    /**
     * Saves or update instance info.
     *
     * @param instanceInfo - instance info
     */
    public synchronized void saveOrUpdateInstance(InstanceInfo instanceInfo) {
        log.debug("Starting to update instances [{}] with ip [{}] for app name [{}]", instanceInfo.getId(),
                instanceInfo.getIPAddr(), instanceInfo.getAppName());
        var metricsInstanceInfo = getById(instanceInfo.getId());
        if (metricsInstanceInfo == null) {
            metricsInstanceInfo = getFirstDownInstance(instanceInfo.getAppName());
            if (metricsInstanceInfo != null) {
                //rewrite first down instance info for specified app name
                log.info(REWRITE_INSTANCE_LOG_MESSAGE, metricsInstanceInfo.getInstanceId(),
                        metricsInstanceInfo.getIpAddress(), metricsInstanceInfo.getAppName(), instanceInfo.getId(),
                        instanceInfo.getIPAddr());
            } else {
                metricsInstanceInfo = new MetricsInstanceInfo();
                setInstanceNumber(instanceInfo.getAppName(), metricsInstanceInfo);
                instances.add(metricsInstanceInfo);
                log.info("New instance [{}] has been added with ip [{}] for app name [{}]", instanceInfo.getId(),
                        instanceInfo.getIPAddr(), instanceInfo.getAppName());
            }
        }
        updateInstanceInfo(metricsInstanceInfo, instanceInfo);
        log.debug("Instance [{}] has been updated with ip [{}] for app name [{}]", instanceInfo.getId(),
                instanceInfo.getIPAddr(), instanceInfo.getAppName());
    }

    /**
     * Marks instance as down.
     *
     * @param instanceId - instance id
     */
    public synchronized void markAsDown(String instanceId) {
        log.info("Starting to mark instance [{}] as down", instanceId);
        var metricsInstanceInfo = getById(instanceId);
        if (metricsInstanceInfo == null) {
            log.warn("Instance [{}] not found to mark as down", instanceId);
        } else {
            metricsInstanceInfo.setStatus(MetricsInstanceStatus.DOWN);
            log.info("Instance [{}] with ip [{}] has been marked as down for app name [{}]",
                    metricsInstanceInfo.getInstanceId(), metricsInstanceInfo.getIpAddress(),
                    metricsInstanceInfo.getAppName());
        }
    }

    /**
     * Gets all metrics instances.
     *
     * @return instances list
     */
    public List<MetricsInstanceInfo> getInstances() {
        return List.copyOf(instances);
    }

    private void setInstanceNumber(String appName, MetricsInstanceInfo metricsInstanceInfo) {
        int counter = appCounters.getOrDefault(appName, 0);
        appCounters.put(appName, ++counter);
        metricsInstanceInfo.setInstanceNumber(counter);
    }

    private void updateInstanceInfo(MetricsInstanceInfo metricsInstanceInfo, InstanceInfo instanceInfo) {
        metricsInstanceInfo.setAppName(instanceInfo.getAppName());
        metricsInstanceInfo.setInstanceId(instanceInfo.getId());
        metricsInstanceInfo.setHostName(instanceInfo.getHostName());
        metricsInstanceInfo.setIpAddress(instanceInfo.getIPAddr());
        metricsInstanceInfo.setStatus(MetricsInstanceStatus.UP);
        var metaData = Optional.ofNullable(instanceInfo.getMetadata()).orElse(Collections.emptyMap());
        if (metaData.containsKey(MANAGEMENT_PORT_KEY)) {
            int managementPort = Integer.parseInt(metaData.get(MANAGEMENT_PORT_KEY));
            metricsInstanceInfo.setManagementPort(managementPort);
        }
        metricsInstanceInfo.setLastSyncDate(LocalDateTime.now());
    }

    private MetricsInstanceInfo getById(String id) {
        return instances.stream()
                .filter(instanceDataInfo -> instanceDataInfo.getInstanceId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private MetricsInstanceInfo getFirstDownInstance(String appName) {
        return instances.stream()
                .filter(instanceDataInfo -> instanceDataInfo.getAppName().equals(appName)
                        && MetricsInstanceStatus.DOWN.equals(instanceDataInfo.getStatus()))
                .findFirst()
                .orElse(null);
    }
}
