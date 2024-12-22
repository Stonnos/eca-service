package com.ecaservice.s3.client.minio.service;

import com.ecaservice.s3.client.minio.TestObject;
import com.ecaservice.s3.client.minio.config.MinioClientAutoConfiguration;
import com.ecaservice.s3.client.minio.databind.JsonDeserializer;
import com.ecaservice.s3.client.minio.databind.JsonSerializer;
import com.ecaservice.s3.client.minio.metrics.MinioStorageMetricsService;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ObjectStorageService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(MinioClientAutoConfiguration.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application-it.properties")
class ObjectStorageServiceIT {

    private static final int Y = 3;
    private static final int X = 2;
    private static final int EXPIRATION_TIME = 7;
    private static final String OBJECT_PATH_FORMAT = "object-%s.json";

    @MockBean
    private MinioStorageMetricsService minioStorageMetricsService;

    @Autowired
    private ObjectStorageService objectStorageService;

    @Test
    void testUploadObject() throws IOException, ClassNotFoundException {
        String objectPath = String.format(OBJECT_PATH_FORMAT, UUID.randomUUID());
        TestObject sourceObject = uploadObject(objectPath);
        TestObject targetObject =
                objectStorageService.getObject(objectPath, TestObject.class, new JsonDeserializer<>());
        assertThat(targetObject).isNotNull();
        assertThat(targetObject.getX()).isEqualTo(sourceObject.getX());
        assertThat(targetObject.getY()).isEqualTo(sourceObject.getY());
    }

    @Test
    void testRemoveObject() throws IOException {
        String objectPath = String.format(OBJECT_PATH_FORMAT, UUID.randomUUID());
        uploadObject(objectPath);
        objectStorageService.removeObject(objectPath);
    }

    @Test
    void testRemoveNotExistingObject() {
        String objectPath = String.format(OBJECT_PATH_FORMAT, UUID.randomUUID());
        objectStorageService.removeObject(objectPath);
    }

    @Test
    void testGetPresignedUrl() throws IOException {
        String objectPath = String.format(OBJECT_PATH_FORMAT, UUID.randomUUID());
        uploadObject(objectPath);
        String presignedUrl = objectStorageService.getObjectPresignedProxyUrl(
                GetPresignedUrlObject.builder()
                        .objectPath(objectPath)
                        .expirationTime(EXPIRATION_TIME)
                        .expirationTimeUnit(TimeUnit.DAYS)
                        .build()
        );
        assertThat(presignedUrl).isNotNull();
    }


    private TestObject uploadObject(String objectPath) throws IOException {
        var sourceObject = new TestObject(X, Y);
        objectStorageService.uploadObject(sourceObject, objectPath, new JsonSerializer());
        return sourceObject;
    }
}
