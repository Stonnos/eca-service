package com.ecaservice.server.service.data;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.data.loader.dto.DataLoaderApiErrorCode;
import com.ecaservice.s3.client.minio.databind.ObjectDeserializer;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.exception.DataLoaderBadRequestException;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createInstancesMetaInfoInfo;
import static com.ecaservice.server.TestHelperUtils.loadInstancesModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link InstancesLoaderService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({InstancesMetaDataService.class, InstancesInfoMapperImpl.class})
class InstancesMetaDataServiceTest {

    @MockBean
    private DataLoaderClient dataLoaderClient;
    @MockBean
    private ObjectStorageService objectStorageService;

    @Autowired
    private InstancesMetaDataService instancesMetaDataService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testLoadInstances() throws IOException, ClassNotFoundException {
        var instancesModel = loadInstancesModel();
        var instancesMetaInfoInfo = createInstancesMetaInfoInfo();
        when(dataLoaderClient.getInstancesMetaInfo(anyString())).thenReturn(instancesMetaInfoInfo);
        when(objectStorageService.getObject(anyString(), any(), any(ObjectDeserializer.class))).thenReturn(instancesModel);
        var instancesMetaDataModel = instancesMetaDataService.getInstancesMetaData(UUID.randomUUID().toString());
        assertThat(instancesMetaDataModel).isNotNull();
        assertThat(instancesMetaDataModel.getRelationName()).isEqualTo(instancesMetaInfoInfo.getRelationName());
        assertThat(instancesMetaDataModel.getClassName()).isEqualTo(instancesMetaInfoInfo.getClassName());
        assertThat(instancesMetaDataModel.getNumInstances()).isEqualTo(instancesMetaInfoInfo.getNumInstances());
        assertThat(instancesMetaDataModel.getNumAttributes()).isEqualTo(instancesMetaInfoInfo.getNumAttributes());
        assertThat(instancesMetaDataModel.getNumClasses()).isEqualTo(instancesMetaInfoInfo.getNumClasses());
        assertThat(instancesMetaDataModel.getMd5Hash()).isEqualTo(instancesMetaInfoInfo.getMd5Hash());
        assertThat(instancesMetaDataModel.getObjectPath()).isEqualTo(instancesMetaInfoInfo.getObjectPath());
    }

    @Test
    void testGetInstancesMetaDataWithServiceUnavailableError() {
        FeignException.ServiceUnavailable serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        when(dataLoaderClient.getInstancesMetaInfo(anyString())).thenThrow(serviceUnavailable);
        assertThrows(InternalServiceUnavailableException.class,
                () -> instancesMetaDataService.getInstancesMetaData(UUID.randomUUID().toString()));
    }

    @Test
    void testGetInstancesMetaDataBadRequest() throws IOException {
        var badRequestEx = mock(FeignException.BadRequest.class);
        var validationError = new ValidationErrorDto();
        validationError.setCode(DataLoaderApiErrorCode.DATA_NOT_FOUND.getCode());
        when(badRequestEx.contentUTF8())
                .thenReturn(objectMapper.writeValueAsString(Collections.singletonList(validationError)));
        when(dataLoaderClient.getInstancesMetaInfo(anyString())).thenThrow(badRequestEx);
        assertThrows(DataLoaderBadRequestException.class,
                () -> instancesMetaDataService.getInstancesMetaData(UUID.randomUUID().toString()));
    }
}
