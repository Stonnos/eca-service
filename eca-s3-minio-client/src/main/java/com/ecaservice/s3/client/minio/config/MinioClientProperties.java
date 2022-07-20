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
     * Default part size is 5MiB in multipart upload
     */
    public static final int DEFAULT_MULTIPART_SIZE = 5 * 1024 * 1024;

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
     * Part size for multipart upload
     */
    private long multipartSize = DEFAULT_MULTIPART_SIZE;

    /**
     * Storage external url
     */
    private String externalUrl;
}
