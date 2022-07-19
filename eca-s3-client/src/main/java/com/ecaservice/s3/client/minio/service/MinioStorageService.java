package com.ecaservice.s3.client.minio.service;

import com.ecaservice.s3.client.minio.config.MinioClientProperties;
import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Minio storage service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService {

    private final MinioClientProperties minioClientProperties;
    private final MinioClient minioClient;

    /**
     * Uploads object to S3 storage.
     *
     * @param inputStream - object input stream
     * @param objectPath  - object path
     */
    public void uploadObject(InputStream inputStream, String objectPath) {
        String bucket = minioClientProperties.getBucketName();
        log.info("Starting to upload object [{}] to s3 minio storage bucket [{}]", objectPath, bucket);
        var putObjectArgs = PutObjectArgs.builder()
                .bucket(bucket)
                .object(objectPath)
                .stream(inputStream, -1L, minioClientProperties.getBatchSize())
                .build();
        try {
            var objectWriteResponse = minioClient.putObject(putObjectArgs);
            log.info("Object [{}] has been uploaded to s3 minio storage bucket [{}] with etag [{}]", objectPath,
                    bucket, objectWriteResponse.etag());
        } catch (Exception ex) {
            log.error("There was an error while upload object [{}] to s3 minio storage bucket [{}]: {}", objectPath,
                    bucket, ex.getMessage());
            throw new ObjectStorageException(ex);
        }
    }
}
