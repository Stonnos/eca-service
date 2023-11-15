package com.ecaservice.server.config;

import lombok.experimental.UtilityClass;

/**
 * Lock registry keys.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class LockRegistryKeys {

    /**
     * Evaluations lock registry key
     */
    public static final String EVALUATION_LOCK_REGISTRY_KEY = "evaluations-registry";

    /**
     * Experiments lock registry key
     */
    public static final String EXPERIMENT_LOCK_REGISTRY_KEY = "experiments-registry";
}
