package com.ecaservice.data.storage.config.audit;

import lombok.experimental.UtilityClass;

/**
 * Audit codes.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class AuditCodes {

    /**
     * Save instances audit code.
     */
    public static final String SAVE_INSTANCES = "SAVE_INSTANCES";

    /**
     * Rename instances audit code.
     */
    public static final String RENAME_INSTANCES = "RENAME_INSTANCES";

    /**
     * Delete instances audit code.
     */
    public static final String DELETE_INSTANCES = "DELETE_INSTANCES";
}
