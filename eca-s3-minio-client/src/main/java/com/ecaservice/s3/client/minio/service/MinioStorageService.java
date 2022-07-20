package com.ecaservice.s3.client.minio.service;

import com.ecaservice.s3.client.minio.config.MinioClientProperties;
import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.model.UploadObject;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;

/**
 * Minio S3 storage service.
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
     * @param uploadObject - upload object
     */
    public void uploadObject(UploadObject uploadObject) {
        String bucket = minioClientProperties.getBucketName();
        log.info("Starting to upload object [{}] to s3 minio storage bucket [{}]. Object size is [{}]",
                uploadObject.getObjectPath(), bucket, uploadObject.getContentLength());
        try {
            @Cleanup var inputStream = uploadObject.getInputStream().get();
            var putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(uploadObject.getObjectPath())
                    .contentType(uploadObject.getContentType())
                    .stream(inputStream, uploadObject.getContentLength(), minioClientProperties.getMultipartSize())
                    .build();
            var objectWriteResponse = minioClient.putObject(putObjectArgs);
            log.info("Object [{}] has been uploaded to s3 minio storage bucket [{}] with etag [{}]",
                    uploadObject.getObjectPath(), bucket, objectWriteResponse.etag());
        } catch (Exception ex) {
            log.error("There was an error while upload object [{}] to s3 minio storage bucket [{}]: {}",
                    uploadObject.getObjectPath(), bucket, ex.getMessage());
            throw new ObjectStorageException(ex);
        }
    }

    /**
     * Download object from S3 storage.
     *
     * @param objectPath - object path
     * @return object input stream
     */
    public InputStream downloadObject(String objectPath) {
        String bucket = minioClientProperties.getBucketName();
        log.info("Starting to download object [{}] from s3 minio storage bucket [{}]", objectPath, bucket);
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioClientProperties.getBucketName())
                            .object(objectPath)
                            .build()
            );
        } catch (Exception ex) {
            log.error("There was an error while download object [{}] from s3 minio storage bucket [{}]: {}",
                    objectPath, bucket, ex.getMessage());
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
        log.info("External presigned url [{}] has been fetched for object path [{}]", objectPresignedExternalUrl,
                objectPath);
        return objectPresignedExternalUrl;
    }
}
