package com.ecaservice.s3.client.minio.service;

import com.ecaservice.s3.client.minio.config.MinioClientProperties;
import com.ecaservice.s3.client.minio.metrics.MinioStorageMetricsService;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.model.UploadObject;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link MinioStorageService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class MinioStorageServiceTest {

    private static final String OBJECT_PATH = "/object";
    private static final int BYTES_ARRAY_LENGTH = 256;
    private static final String BUCKET = "bucket";
    private static final long MULTIPART_SIZE = 5L * 1024L * 1024L;
    private static final String S3_PROXY_URL = "http://s3-proxy";
    private static final int EXPIRATION_TIME = 7;
    private static final String PRESIGNED_URL =
            "http://s3-storage/object/path?X-Amz-Signature=b828e7099ecdcca904db26e36a9de829aa3c7ac08e3fdd269cf75ed5dc21f38b";
    public static final String EXPECTED_PRESIGNED_PROXY_URL =
            "http://s3-proxy/object/path?X-Amz-Signature=b828e7099ecdcca904db26e36a9de829aa3c7ac08e3fdd269cf75ed5dc21f38b";

    @Mock
    private MinioClientProperties minioClientProperties;
    @Mock
    private MinioClient minioClient;
    @Mock
    private MinioStorageMetricsService minioStorageMetricsService;

    @InjectMocks
    private MinioStorageService minioStorageService;

    @Captor
    private ArgumentCaptor<PutObjectArgs> putObjectArgsArgumentCaptor;
    @Captor
    private ArgumentCaptor<GetObjectArgs> getObjectArgsArgumentCaptor;
    @Captor
    private ArgumentCaptor<RemoveObjectArgs> removeObjectArgsArgumentCaptor;
    @Captor
    private ArgumentCaptor<GetPresignedObjectUrlArgs> getPresignedObjectUrlArgsArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> bytesCaptor;

    @BeforeEach
    void init() {
        when(minioClientProperties.getBucketName()).thenReturn(BUCKET);
    }

    @Test
    void testUploadObject() throws Exception {
        var objectWriteResponse = mock(ObjectWriteResponse.class);
        when(minioClientProperties.getMultipartSize()).thenReturn(MULTIPART_SIZE);
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(objectWriteResponse);
        byte[] testBytes = new byte[BYTES_ARRAY_LENGTH];
        var uploadObject = UploadObject.builder()
                .objectPath(OBJECT_PATH)
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .inputStream(() -> new ByteArrayInputStream(testBytes))
                .build();
        minioStorageService.uploadObject(uploadObject);
        // Verify put object params
        verify(minioClient, atLeastOnce()).putObject(putObjectArgsArgumentCaptor.capture());
        var putObjectArgs = putObjectArgsArgumentCaptor.getValue();
        assertThat(putObjectArgs).isNotNull();
        assertThat(putObjectArgs.bucket()).isEqualTo(BUCKET);
        assertThat(putObjectArgs.object()).isEqualTo(uploadObject.getObjectPath());
        assertThat(putObjectArgs.contentType()).isEqualTo(uploadObject.getContentType());
        // Verify track object size
        verify(minioStorageMetricsService, atLeastOnce()).trackObjectSizeBytes(bytesCaptor.capture());
        assertThat(bytesCaptor.getValue()).isEqualTo(testBytes.length);
        // Verify track success request
        verify(minioStorageMetricsService, atLeastOnce()).trackRequestSuccess(anyString());
    }

    @Test
    void testDownloadObject() throws Exception {
        minioStorageService.downloadObject(OBJECT_PATH);
        verify(minioClient, atLeastOnce()).getObject(getObjectArgsArgumentCaptor.capture());
        var getObjectArgs = getObjectArgsArgumentCaptor.getValue();
        assertThat(getObjectArgs).isNotNull();
        assertThat(getObjectArgs.bucket()).isEqualTo(BUCKET);
        assertThat(getObjectArgs.object()).isEqualTo(OBJECT_PATH);
        verify(minioStorageMetricsService, atLeastOnce()).trackRequestSuccess(anyString());
    }

    @Test
    void testRemoveObject() throws Exception {
        minioStorageService.removeObject(OBJECT_PATH);
        verify(minioClient, atLeastOnce()).removeObject(removeObjectArgsArgumentCaptor.capture());
        var removeObjectArgs = removeObjectArgsArgumentCaptor.getValue();
        assertThat(removeObjectArgs).isNotNull();
        assertThat(removeObjectArgs.bucket()).isEqualTo(BUCKET);
        assertThat(removeObjectArgs.object()).isEqualTo(OBJECT_PATH);
        verify(minioStorageMetricsService, atLeastOnce()).trackRequestSuccess(anyString());
    }

    @Test
    void testGetPresignedProxyUrl() throws Exception {
        when(minioClientProperties.getProxyUrl()).thenReturn(S3_PROXY_URL);
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn(PRESIGNED_URL);
        var getPresignedUrlObject = GetPresignedUrlObject.builder()
                .objectPath(OBJECT_PATH)
                .expirationTime(EXPIRATION_TIME)
                .expirationTimeUnit(TimeUnit.DAYS)
                .build();
        String presignedUrl = minioStorageService.getObjectPresignedProxyUrl(getPresignedUrlObject);
        //Verify request params
        verify(minioClient, atLeastOnce()).getPresignedObjectUrl(getPresignedObjectUrlArgsArgumentCaptor.capture());
        var getPresignedObjectUrlArgs = getPresignedObjectUrlArgsArgumentCaptor.getValue();
        assertThat(getPresignedObjectUrlArgs).isNotNull();
        assertThat(getPresignedObjectUrlArgs.bucket()).isEqualTo(BUCKET);
        assertThat(getPresignedObjectUrlArgs.object()).isEqualTo(getPresignedUrlObject.getObjectPath());
        assertThat(getPresignedObjectUrlArgs.method()).isEqualTo(Method.GET);
        //Verify target presigned url
        assertThat(presignedUrl).isEqualTo(EXPECTED_PRESIGNED_PROXY_URL);
    }
}
