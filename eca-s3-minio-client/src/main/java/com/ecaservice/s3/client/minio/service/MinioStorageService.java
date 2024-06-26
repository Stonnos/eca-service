package com.ecaservice.s3.client.minio.service;

import com.ecaservice.s3.client.minio.config.MinioClientProperties;
import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.metrics.MinioStorageMetricsService;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.model.UploadObject;
import io.micrometer.core.annotation.Timed;
import io.micrometer.tracing.annotation.NewSpan;
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

import static com.ecaservice.s3.client.minio.metrics.MetricConstants.OBJECT_REQUEST_METRIC;

/**
 * Minio S3 storage service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService {

    private static final String UPLOAD_OBJECT_METHOD = "uploadObject";
    private static final String DOWNLOAD_OBJECT_METHOD = "downloadObject";
    private static final String REMOVE_OBJECT_METHOD = "removeObject";
    private static final String GET_OBJECT_PRESIGNED_URL_METHOD = "getObjectPresignedUrl";

    private final MinioClientProperties minioClientProperties;
    private final MinioClient minioClient;
    private final MinioStorageMetricsService minioStorageMetricsService;

    /**
     * Uploads object to S3 storage.
     *
     * @param uploadObject - upload object
     */
    @NewSpan
    @Timed(value = OBJECT_REQUEST_METRIC)
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
            minioStorageMetricsService.trackObjectSizeBytes(contentLength);
            var putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(uploadObject.getObjectPath())
                    .contentType(uploadObject.getContentType())
                    .stream(inputStream, contentLength, minioClientProperties.getMultipartSize())
                    .build();
            var objectWriteResponse = minioClient.putObject(putObjectArgs);
            stopWatch.stop();
            minioStorageMetricsService.trackRequestSuccess(UPLOAD_OBJECT_METHOD);
            log.info("Object [{}] has been uploaded to s3 minio storage bucket [{}] with etag [{}]. Total time [{}] s.",
                    uploadObject.getObjectPath(), bucket, objectWriteResponse.etag(), stopWatch.getTotalTimeSeconds());
        } catch (Exception ex) {
            log.error("There was an error while upload object [{}] to s3 minio storage bucket [{}]: {}",
                    uploadObject.getObjectPath(), bucket, ex.getMessage());
            minioStorageMetricsService.trackRequestError(UPLOAD_OBJECT_METHOD);
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
            var inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioClientProperties.getBucketName())
                            .object(objectPath)
                            .build()
            );
            minioStorageMetricsService.trackRequestSuccess(DOWNLOAD_OBJECT_METHOD);
            return inputStream;
        } catch (Exception ex) {
            log.error("There was an error while download object [{}] from s3 minio storage bucket [{}]: {}",
                    objectPath, bucket, ex.getMessage());
            minioStorageMetricsService.trackRequestError(DOWNLOAD_OBJECT_METHOD);
            throw new ObjectStorageException(ex);
        }
    }

    /**
     * Removes object from S3 storage.
     *
     * @param objectPath - object path
     */
    @NewSpan
    @Timed(value = OBJECT_REQUEST_METRIC)
    public void removeObject(String objectPath) {
        String bucket = minioClientProperties.getBucketName();
        log.info("Starting to remove object [{}] from s3 minio storage bucket [{}]", objectPath, bucket);
        try {
            var stopWatch = new StopWatch();
            stopWatch.start();
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioClientProperties.getBucketName())
                            .object(objectPath)
                            .build()
            );
            stopWatch.stop();
            minioStorageMetricsService.trackRequestSuccess(REMOVE_OBJECT_METHOD);
            log.info("Object [{}] has been removed from s3 minio storage bucket [{}] for {} s.", objectPath, bucket,
                    stopWatch.getTotalTimeSeconds());
        } catch (Exception ex) {
            log.error("There was an error while remove object [{}] from s3 minio storage bucket [{}]: {}",
                    objectPath, bucket, ex.getMessage());
            minioStorageMetricsService.trackRequestError(REMOVE_OBJECT_METHOD);
            throw new ObjectStorageException(ex);
        }
    }

    /**
     * Gets S3 presigned url for object.
     *
     * @param presignedUrlObject - presigned url object
     * @return presigned url
     */
    @NewSpan
    @Timed(value = OBJECT_REQUEST_METRIC)
    public String getObjectPresignedUrl(GetPresignedUrlObject presignedUrlObject) {
        log.info("Gets presigned url for object path [{}]", presignedUrlObject.getObjectPath());
        try {
            var stopWatch = new StopWatch();
            stopWatch.start();
            var objectPresignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(minioClientProperties.getBucketName())
                            .object(presignedUrlObject.getObjectPath())
                            .expiry(presignedUrlObject.getExpirationTime(), presignedUrlObject.getExpirationTimeUnit())
                            .method(Method.GET)
                            .build()
            );
            stopWatch.stop();
            minioStorageMetricsService.trackRequestSuccess(GET_OBJECT_PRESIGNED_URL_METHOD);
            log.info("Presigned url [{}] has been fetched for object path [{}]. Total time {} s", objectPresignedUrl,
                    presignedUrlObject, stopWatch.getTotalTimeSeconds());
            return objectPresignedUrl;
        } catch (Exception ex) {
            log.error("There was an error while get presigned url for object path [{}]: {}", presignedUrlObject,
                    ex.getMessage());
            minioStorageMetricsService.trackRequestError(GET_OBJECT_PRESIGNED_URL_METHOD);
            throw new ObjectStorageException(ex);
        }
    }

    /**
     * Gets S3 presigned proxy url for object.
     *
     * @param presignedUrlObject - presigned url object
     * @return presigned proxy url
     */
    @NewSpan
    @Timed(value = OBJECT_REQUEST_METRIC)
    public String getObjectPresignedProxyUrl(GetPresignedUrlObject presignedUrlObject) {
        log.info("Gets presigned proxy url for object path [{}]", presignedUrlObject.getObjectPath());
        var objectPresignedUrl = getObjectPresignedUrl(presignedUrlObject);
        var url = UriComponentsBuilder.fromHttpUrl(objectPresignedUrl).build();
        var objectPresignedProxyUrl = String.format("%s%s?%s", minioClientProperties.getProxyUrl(),
                url.getPath(), url.getQuery());
        log.info("Proxy presigned url [{}] has been fetched for object path [{}]", objectPresignedProxyUrl,
                presignedUrlObject.getObjectPath());
        return objectPresignedProxyUrl;
    }
}
