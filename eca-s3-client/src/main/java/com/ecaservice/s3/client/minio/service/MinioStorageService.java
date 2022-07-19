package com.ecaservice.s3.client.minio.service;

import com.ecaservice.s3.client.minio.config.MinioClientProperties;
import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

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
        try {
            var putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectPath)
                    .stream(inputStream, -1L, minioClientProperties.getBatchSize())
                    .build();
            var objectWriteResponse = minioClient.putObject(putObjectArgs);
            log.info("Object [{}] has been uploaded to s3 minio storage bucket [{}] with etag [{}]", objectPath,
                    bucket, objectWriteResponse.etag());
        } catch (Exception ex) {
            log.error("There was an error while upload object [{}] to s3 minio storage bucket [{}]: {}", objectPath,
                    bucket, ex.getMessage());
            throw new ObjectStorageException(ex);
        }
    }

    /**
     * Gets S3 presigned url for object.
     *
     * @param objectPath - object path
     * @return presigned url
     */
    public String getObjectPresignedUrl(String objectPath) {
        log.info("Gets presigned url for object path [{}]", objectPath);
        try {
            var objectPresignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(minioClientProperties.getBucketName())
                            .object(objectPath)
                            .method(Method.GET)
                            .build()
            );
            log.info("Presigned url [{}] has been fetched for object path [{}]", objectPresignedUrl, objectPath);
            return objectPresignedUrl;
        } catch (Exception ex) {
            log.error("There was an error while get presigned url for object path [{}]: {}", objectPath,
                    ex.getMessage());
            throw new ObjectStorageException(ex);
        }
    }

    /**
     * Gets S3 presigned external url for object.
     *
     * @param objectPath - object path
     * @return presigned external url
     */
    public String getObjectPresignedExternalUrl(String objectPath) {
        log.info("Gets presigned external url for object path [{}]", objectPath);
        var objectPresignedUrl = getObjectPresignedUrl(objectPath);
        var url = UriComponentsBuilder.fromHttpUrl(objectPresignedUrl).build();
        var objectPresignedExternalUrl = String.format("%s/%s?%s", minioClientProperties.getExternalUrl(),
                url.getPath(), url.getQuery());
        log.info("External presigned url [{}] has been fetched for object path [{}]", objectPresignedUrl, objectPath);
        return objectPresignedUrl;
    }
}
