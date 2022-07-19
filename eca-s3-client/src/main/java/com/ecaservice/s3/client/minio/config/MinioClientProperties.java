package com.ecaservice.s3.client.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Minio properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("minio")
public class MinioClientProperties {

    /**
     * Storage url
     */
    private String url;

    /**
     * Access key
     */
    private String accessKey;

    /**
     * Secret key
     */
    private String secretKey;

    /**
     * Bucket name
     */
    private String bucketName;

    /**
     * Batch size for multipart upload
     */
    private long batchSize;

    /**
     * Storage external url
     */
    private String externalUrl;
}
