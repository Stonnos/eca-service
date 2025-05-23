package com.ecaservice.s3.client.minio.service;

import com.ecaservice.s3.client.minio.config.MinioClientProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Minio bucket creator.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "minio.autoCreateBucket", havingValue = "true")
@RequiredArgsConstructor
public class MinioBucketCreator {

    private static final long WAIT_TIME_MILLIS = 3000L;
    private static final long INITIAL_DELAY = 1000L;

    private final MinioClientProperties minioClientProperties;
    private final MinioClient minioClient;

    private ScheduledFuture<?> scheduledFuture;

    /**
     * Create bucket in minio storage.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void createBucket() {
        log.info("Starting job to create bucket [{}]", minioClientProperties.getBucketName());
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Runnable createBucketTask = createBucketTask();
        scheduledFuture =
                scheduledExecutorService.scheduleAtFixedRate(createBucketTask, INITIAL_DELAY, WAIT_TIME_MILLIS,
                        TimeUnit.MILLISECONDS);
    }

    private Runnable createBucketTask() {
        LocalDateTime timeoutTimestamp =
                LocalDateTime.now().plusMinutes(minioClientProperties.getAutoCreateBucketTimeoutMinutes());
        return () -> {
            log.info("Starting task to create bucket [{}]", minioClientProperties.getBucketName());
            try {
                if (LocalDateTime.now().isAfter(timeoutTimestamp)) {
                    log.warn("Timeout while bucket [{}] creation", minioClientProperties.getBucketName());
                } else {
                    BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                            .bucket(minioClientProperties.getBucketName())
                            .build();
                    if (minioClient.bucketExists(bucketExistsArgs)) {
                        log.info("Bucket [{}] already exists. Skip bucket creation",
                                minioClientProperties.getBucketName());
                    } else {
                        log.info("Attempt to create bucket [{}]", minioClientProperties.getBucketName());
                        MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                                .bucket(minioClientProperties.getBucketName())
                                .build();
                        minioClient.makeBucket(makeBucketArgs);
                        log.info("Bucket [{}] has been created", minioClientProperties.getBucketName());
                    }
                }
                // Finish scheduled task
                scheduledFuture.cancel(true);
            } catch (Exception ex) {
                log.error("Error while create bucket [{}]: {}", minioClientProperties.getBucketName(),
                        ex.getMessage());
            }
        };
    }
}
