package com.ecaservice.s3.client.minio.model;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 * Get presigned url model.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class GetPresignedUrlObject {

    /**
     * Object path
     */
    private String objectPath;

    /**
     * Url expiration time
     */
    private int expirationTime;

    /**
     * Expiration time unit
     */
    private TimeUnit expirationTimeUnit;
}
