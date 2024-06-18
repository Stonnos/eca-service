package com.ecaservice.data.storage.service.data;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.data.loader.dto.DataLoaderApiErrorCode;
import com.ecaservice.data.loader.dto.UploadInstancesResponseDto;
import com.ecaservice.data.storage.exception.DataLoaderBadRequestException;
import com.ecaservice.data.storage.service.api.DataLoaderApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.model.InstancesModel;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.data.storage.TestHelperUtils.loadInstancesModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UploadInstancesObjectService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({UploadInstancesObjectService.class, ObjectMapper.class})
class UploadInstancesObjectServiceTest {

    @MockBean
    private DataLoaderApiClient dataLoaderApiClient;

    @Autowired
    private UploadInstancesObjectService uploadInstancesObjectService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private InstancesModel instancesModel;

    @BeforeEach
    void init() throws IOException {
        instancesModel = loadInstancesModel();
    }

    @Test
    void testLoadInstances() throws IOException {
        var instancesModel = loadInstancesModel();
        UploadInstancesResponseDto uploadInstancesResponseDto = UploadInstancesResponseDto.builder()
                .uuid(UUID.randomUUID().toString())
                .build();
        when(dataLoaderApiClient.uploadInstances(any(MultipartFile.class))).thenReturn(uploadInstancesResponseDto);
        var actualResponse =
                uploadInstancesObjectService.uploadInstances(UUID.randomUUID().toString(), instancesModel);
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getUuid()).isNotNull();
    }

    @Test
    void testUploadInstancesWithServiceUnavailableError() {
        FeignException.ServiceUnavailable serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        when(dataLoaderApiClient.uploadInstances(any(MultipartFile.class))).thenThrow(serviceUnavailable);
        assertThrows(InternalServiceUnavailableException.class,
                () -> uploadInstancesObjectService.uploadInstances(UUID.randomUUID().toString(), instancesModel));
    }

    @Test
    void testUploadInstancesBadRequest() throws IOException {
        var badRequestEx = mock(FeignException.BadRequest.class);
        var validationError = new ValidationErrorDto();
        validationError.setCode(DataLoaderApiErrorCode.DATA_NOT_FOUND.getCode());
        when(badRequestEx.contentUTF8())
                .thenReturn(objectMapper.writeValueAsString(Collections.singletonList(validationError)));
        when(dataLoaderApiClient.uploadInstances(any(MultipartFile.class))).thenThrow(badRequestEx);
        assertThrows(DataLoaderBadRequestException.class,
                () -> uploadInstancesObjectService.uploadInstances(UUID.randomUUID().toString(), instancesModel));
    }
}
