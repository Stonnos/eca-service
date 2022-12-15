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
    public static final String APP_NAME_LABEL_NAME = "__meta_discovery_app_name";

    /**
     * Application instance label
     */
    public static final String APP_INSTANCE_LABEL_NAME = "__meta_discovery_app_instance_name";

    /**
     * App instance metrics path label
     */
    public static final String APP_INSTANCE_METRICS_PATH_LABEL_NAME = "__meta_discovery_app_instance_metrics_path";
}
