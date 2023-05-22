package com.ecaservice.data.storage.exception;

/**
 * Instances report exception.
 *
 * @author Roman Batygin
 */
public class InstancesReportException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param ex - exception
     */
    public InstancesReportException(Exception ex) {
        super(ex);
    }
}
