package com.ecaservice.s3.client.minio.service;

import com.ecaservice.s3.client.minio.databind.FstDeserializer;
import com.ecaservice.s3.client.minio.databind.FstSerializer;
import com.ecaservice.s3.client.minio.databind.ObjectDeserializer;
import com.ecaservice.s3.client.minio.databind.ObjectSerializer;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.model.UploadObject;
import io.micrometer.core.annotation.Timed;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;

import static com.ecaservice.s3.client.minio.metrics.MetricConstants.OBJECT_REQUEST_METRIC;

/**
 * Object storage service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private final MinioStorageService minioStorageService;

    /**
     * Uploads serialized object to S3 storage. Default {@link FstSerializer} used by default.
     *
     * @param object     - object
     * @param objectPath - object path in S3
     * @throws IOException in case of I/O error
     */
    public void uploadObject(Serializable object, String objectPath) throws IOException {
        uploadObject(object, objectPath, new FstSerializer());
    }

    /**
     * Uploads serialized object to S3 storage.
     *
     * @param object     - object
     * @param objectPath - object path in S3
     * @param serializer - object serializer
     * @throws IOException in case of I/O error
     */
    @NewSpan
    public void uploadObject(Serializable object, String objectPath, ObjectSerializer serializer) throws IOException {
        log.info("Starting to upload object [{}] to storage", objectPath);
        log.info("Starting to serialize object [{}]", objectPath);
        var stopWatch = new StopWatch();
        stopWatch.start();
        byte[] bytes = serializer.serialize(object);
        stopWatch.stop();
        log.info("Object [{}] has been serialized for {} s.", objectPath, stopWatch.getTotalTimeSeconds());
        minioStorageService.uploadObject(
                UploadObject.builder()
                        .objectPath(objectPath)
                        .inputStream(() -> new ByteArrayInputStream(bytes))
                        .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .build()
        );
    }

    /**
     * Gets object from S3 storage. Default {@link FstDeserializer} used by default.
     *
     * @param objectPath  - object path
     * @param targetClazz - target class
     * @param <T>         - object generic type
     * @return original object
     * @throws IOException            in case of I/O error
     * @throws ClassNotFoundException in case of class not found errors
     */
    @NewSpan
    @Timed(value = OBJECT_REQUEST_METRIC)
    public <T> T getObject(String objectPath, Class<T> targetClazz) throws IOException, ClassNotFoundException {
        return getObject(objectPath, targetClazz, new FstDeserializer<>());
    }

    /**
     * Gets object from S3 storage.
     *
     * @param objectPath         - object path
     * @param targetClazz        - target class
     * @param objectDeserializer - object deserializer
     * @param <T>                - object generic type
     * @return original object
     * @throws IOException            in case of I/O error
     * @throws ClassNotFoundException in case of class not found errors
     */
    @NewSpan
    @Timed(value = OBJECT_REQUEST_METRIC)
    public <T> T getObject(String objectPath, Class<T> targetClazz, ObjectDeserializer<T> objectDeserializer)
            throws IOException, ClassNotFoundException {
        log.info("Starting to get object [{}] with class [{}] from storage", objectPath, targetClazz.getName());
        var stopWatch = new StopWatch();
        stopWatch.start();
        @Cleanup var inputStream = minioStorageService.downloadObject(objectPath);
        T targetObject = objectDeserializer.deserialize(inputStream, targetClazz);
        stopWatch.stop();
        log.info("Object [{}] with class [{}] has been fetched from storage for {} s.", objectPath,
                targetClazz.getName(), stopWatch.getTotalTimeSeconds());
        return targetObject;
    }

    /**
     * Gets S3 presigned proxy url for object.
     *
     * @param presignedUrlObject - presigned url object
     * @return presigned proxy url
     */
    @NewSpan
    public String getObjectPresignedProxyUrl(GetPresignedUrlObject presignedUrlObject) {
        return minioStorageService.getObjectPresignedProxyUrl(presignedUrlObject);
    }

    /**
     * Removes object from S3 storage.
     *
     * @param objectPath - object path
     */
    @NewSpan
    public void removeObject(String objectPath) {
        minioStorageService.removeObject(objectPath);
    }
}
