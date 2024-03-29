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

    /**
     * Downloads instances report
     */
    public static final String DOWNLOAD_INSTANCES_REPORT = "DOWNLOAD_INSTANCES_REPORT";

    /**
     * Sets class attribute.
     */
    public static final String SET_CLASS_ATTRIBUTE = "SET_CLASS_ATTRIBUTE";

    /**
     * Select attribute
     */
    public static final String SELECT_ATTRIBUTE = "SELECT_ATTRIBUTE";

    /**
     * Unselect attribute
     */
    public static final String UNSELECT_ATTRIBUTE = "UNSELECT_ATTRIBUTE";

    /**
     * Select all attributes
     */
    public static final String SELECT_ALL_ATTRIBUTES = "SELECT_ALL_ATTRIBUTES";
}
