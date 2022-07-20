package com.ecaservice.s3.client.minio.service;

import com.ecaservice.s3.client.minio.model.UploadObject;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.io.Serializable;

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
     * Uploads serialized object to S3 storage.
     *
     * @param object     - object
     * @param objectPath - object path in S3
     * @throws IOException in case of I/O error
     */
    public void uploadObject(Serializable object, String objectPath) throws IOException {
        log.info("Starting to upload object [{}] to storage", objectPath);
        log.info("Starting to serialize object [{}]", objectPath);
        var stopWatch = new StopWatch();
        stopWatch.start();
        @Cleanup var outputStream = new FastByteArrayOutputStream();
        @Cleanup var out = new FSTObjectOutput(outputStream);
        out.writeObject(object);
        stopWatch.stop();
        log.info("Object [{}] has been serialized for {} s.", objectPath, stopWatch.getTotalTimeSeconds());
        minioStorageService.uploadObject(
                UploadObject.builder()
                        .objectPath(objectPath)
                        .inputStream(outputStream::getInputStream)
                        .contentLength(outputStream.size())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .build()
        );
        outputStream.reset();
    }

    /**
     * Gets object from S3 storage.
     *
     * @param objectPath  - object path
     * @param targetClazz - target class
     * @param <T>         - object generic type
     * @return original object
     * @throws IOException            in case of I/O error
     * @throws ClassNotFoundException in case of class not found errors
     */
    public <T> T getObject(String objectPath, Class<T> targetClazz) throws IOException, ClassNotFoundException {
        log.info("Starting to get object [{}] with class [{}] from storage", objectPath, targetClazz.getName());
        var stopWatch = new StopWatch();
        stopWatch.start();
        @Cleanup var inputStream = minioStorageService.downloadObject(objectPath);
        @Cleanup var in = new FSTObjectInput(inputStream);
        Object readObject = in.readObject();
        T targetObject = targetClazz.cast(readObject);
        stopWatch.stop();
        log.info("Object [{}] with class [{}] has been fetched from storage for {} s.", objectPath,
                targetClazz.getName(), stopWatch.getTotalTimeSeconds());
        return targetObject;
    }

    /**
     * Gets S3 presigned proxy url for object.
     *
     * @param objectPath - object path
     * @return presigned proxy url
     */
    public String getObjectPresignedProxyUrl(String objectPath) {
        return minioStorageService.getObjectPresignedProxyUrl(objectPath);
    }
}
