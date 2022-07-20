package com.ecaservice.s3.client.minio.exception;

/**
 * Object storage exception.
 *
 * @author Roman Batygin
 */
public class ObjectStorageException extends RuntimeException {

    /**
     * Creates exception object
     *
     * @param ex - cause throwable
     */
    public ObjectStorageException(Throwable ex) {
        super(ex);
    }
}
