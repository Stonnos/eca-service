package com.ecaservice.s3.client.minio.model;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * Upload object model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class UploadObject {

    /**
     * Input stream supplier
     */
    private Supplier<InputStream> inputStream;

    /**
     * Object path
     */
    private String objectPath;

    /**
     * Content type
     */
    private String contentType;

    /**
     * Content length
     */
    private long contentLength;
}
