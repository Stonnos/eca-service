package com.ecaservice.s3.client.minio.service;

import com.ecaservice.s3.client.minio.ObjectStorageTestDataProvider;
import com.ecaservice.s3.client.minio.TestHelperUtils;
import com.ecaservice.s3.client.minio.metrics.MinioStorageMetricsService;
import com.ecaservice.s3.client.minio.model.UploadObject;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ObjectStorageService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ObjectStorageServiceTest {

    private static final String OBJECT_PATH = "/object";

    @Mock
    private MinioStorageService minioStorageService;
    @Mock
    private MinioStorageMetricsService minioStorageMetricsService;

    @InjectMocks
    private ObjectStorageService objectStorageService;

    @Captor
    private ArgumentCaptor<UploadObject> uploadObjectArgsArgumentCaptor;

    @ParameterizedTest
    @ArgumentsSource(ObjectStorageTestDataProvider.class)
    void testUploadAndDownloadObject(TestHelperUtils.TestObject testObject) throws IOException, ClassNotFoundException {
        objectStorageService.uploadObject(testObject, OBJECT_PATH);
        verify(minioStorageService, atLeastOnce()).uploadObject(uploadObjectArgsArgumentCaptor.capture());
        var uploadObject = uploadObjectArgsArgumentCaptor.getValue();
        assertThat(uploadObject).isNotNull();
        assertThat(uploadObject.getObjectPath()).isEqualTo(OBJECT_PATH);
        assertThat(uploadObject.getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // Downloads object and verify results
        when(minioStorageService.downloadObject(OBJECT_PATH)).thenReturn(uploadObject.getInputStream().get());
        var actualObject = objectStorageService.getObject(OBJECT_PATH, TestHelperUtils.TestObject.class);
        assertThat(testObject).isEqualTo(actualObject);
    }
}
