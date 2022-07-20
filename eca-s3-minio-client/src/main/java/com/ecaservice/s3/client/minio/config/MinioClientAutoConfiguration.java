package com.ecaservice.s3.client.minio.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * S3 minio client auto configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@ComponentScan({"com.ecaservice.s3.client.minio"})
@EnableConfigurationProperties(MinioClientProperties.class)
public class MinioClientAutoConfiguration {

    /**
     * Creates minio client bean.
     *
     * @param minioClientProperties - minio client properties
     * @return minio client bean
     */
    @Bean
    public MinioClient minioClient(MinioClientProperties minioClientProperties) {
        log.info("Starting to initialize minio client");
        var minioClient = MinioClient.builder()
                .endpoint(minioClientProperties.getUrl())
                .credentials(minioClientProperties.getAccessKey(), minioClientProperties.getSecretKey())
                .build();
        log.info("Minio client has been initialized. S3 minio url: {}", minioClientProperties.getUrl());
        return minioClient;
    }
}
