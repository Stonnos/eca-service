package com.ecaservice.s3.client.minio.service;

import com.ecaservice.s3.client.minio.config.MinioClientProperties;
import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.model.UploadObject;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
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
        log.info("Starting to upload object [{}] to s3 minio storage bucket [{}]", uploadObject.getObjectPath(),
                bucket);
        try {
            var stopWatch = new StopWatch();
            stopWatch.start();
            @Cleanup var inputStream = uploadObject.getInputStream().get();
            long contentLength = inputStream.available();
            log.info("Object [{}] size is {} bytes", uploadObject.getObjectPath(), contentLength);
            var putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(uploadObject.getObjectPath())
                    .contentType(uploadObject.getContentType())
                    .stream(inputStream, contentLength, minioClientProperties.getMultipartSize())
                    .build();
            var objectWriteResponse = minioClient.putObject(putObjectArgs);
            stopWatch.stop();
            log.info("Object [{}] has been uploaded to s3 minio storage bucket [{}] with etag [{}]. Total time [{}] s.",
                    uploadObject.getObjectPath(), bucket, objectWriteResponse.etag(), stopWatch.getTotalTimeSeconds());
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
     * Removes object from S3 storage.
     *
     * @param objectPath - object path
     */
    public void removeObject(String objectPath) {
        String bucket = minioClientProperties.getBucketName();
        log.info("Starting to remove object [{}] from s3 minio storage bucket [{}]", objectPath, bucket);
        try {
            var stopWatch = new StopWatch();
            stopWatch.start();
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(objectPath)
                            .object(objectPath)
                            .build()
            );
            stopWatch.stop();
            log.info("Object [{}] has been removed from s3 minio storage bucket [{}] for {} s.", objectPath, bucket,
                    stopWatch.getTotalTimeSeconds());
        } catch (Exception ex) {
            log.error("There was an error while remove object [{}] from s3 minio storage bucket [{}]: {}",
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
            var stopWatch = new StopWatch();
            stopWatch.start();
            var objectPresignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(minioClientProperties.getBucketName())
                            .object(objectPath)
                            .method(Method.GET)
                            .build()
            );
            stopWatch.stop();
            log.info("Presigned url [{}] has been fetched for object path [{}]. Total time {} s", objectPresignedUrl,
                    objectPath, stopWatch.getTotalTimeSeconds());
            return objectPresignedUrl;
        } catch (Exception ex) {
            log.error("There was an error while get presigned url for object path [{}]: {}", objectPath,
                    ex.getMessage());
            throw new ObjectStorageException(ex);
        }
    }

    /**
     * Gets S3 presigned proxy url for object.
     *
     * @param objectPath - object path
     * @return presigned proxy url
     */
    public String getObjectPresignedProxyUrl(String objectPath) {
        log.info("Gets presigned proxy url for object path [{}]", objectPath);
        var objectPresignedUrl = getObjectPresignedUrl(objectPath);
        var url = UriComponentsBuilder.fromHttpUrl(objectPresignedUrl).build();
        var objectPresignedProxyUrl = String.format("%s/%s?%s", minioClientProperties.getProxyUrl(),
                url.getPath(), url.getQuery());
        log.info("Proxy presigned url [{}] has been fetched for object path [{}]", objectPresignedProxyUrl,
                objectPath);
        return objectPresignedProxyUrl;
    }
}
