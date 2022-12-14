package com.ecaservice.discovery.prometheus;

import lombok.experimental.UtilityClass;

/**
 * Labels for prometheus.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Labels {

    /**
     * Application name label
     */
    public static final String APP_NAME_LABEL_NAME = "__meta_discovery_app_name__";

    /**
     * Application instance label
     */
    public static final String APP_INSTANCES_LABEL_NAME = "__meta_discovery_app_instance_name__";
}
