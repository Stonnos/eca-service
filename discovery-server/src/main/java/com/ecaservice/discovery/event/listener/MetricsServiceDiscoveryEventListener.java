package com.ecaservice.discovery.event.listener;

import com.ecaservice.discovery.service.MetricsDiscoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Metrics service discovery event listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsServiceDiscoveryEventListener {

    private final MetricsDiscoveryService metricsDiscoveryService;

    /**
     * Handles eureka instance registered event.
     *
     * @param event - eureka instance event
     */
    @EventListener
    public void handle(EurekaInstanceRegisteredEvent event) {
        var instance = event.getInstanceInfo();
        log.debug("Registered instance [{}], id [{}], ip [{}]", instance.getAppName(), instance.getInstanceId(),
                instance.getIPAddr());
        metricsDiscoveryService.saveOrUpdateInstance(instance);
    }

    /**
     * Handles eureka renewed registered event.
     *
     * @param event - eureka instance event
     */
    @EventListener
    public void handle(EurekaInstanceRenewedEvent event) {
        var instance = event.getInstanceInfo();
        if (instance != null) {
            log.debug("Renewed instance [{}], id [{}], ip [{}]", instance.getAppName(), instance.getInstanceId(),
                    instance.getIPAddr());
            metricsDiscoveryService.saveOrUpdateInstance(instance);
        }
    }

    /**
     * Handles eureka instance canceled event.
     *
     * @param event - eureka instance event
     */
    @EventListener
    public void handle(EurekaInstanceCanceledEvent event) {
        log.debug("Cancel instance [{}] for app name [{}]", event.getServerId(), event.getAppName());
        metricsDiscoveryService.markAsDown(event.getServerId());
    }
}
