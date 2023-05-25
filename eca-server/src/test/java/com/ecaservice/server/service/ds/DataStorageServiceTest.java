package com.ecaservice.server.service.ds;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.data.storage.dto.DsInternalApiErrorCode;
import com.ecaservice.server.exception.DataStorageBadRequestException;
import com.ecaservice.server.service.DataStorageLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.converter.InstancesConverter;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.loadInstancesModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DataStorageService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({DataStorageService.class, DataStorageErrorHandler.class, InstancesConverter.class})
class DataStorageServiceTest {

    @MockBean
    private DataStorageLoader dataStorageLoader;

    @Inject
    private DataStorageService dataStorageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testGetValidInstances() throws IOException {
        var instancesModel = loadInstancesModel();
        when(dataStorageLoader.downloadValidInstancesReport(anyString())).thenReturn(instancesModel);
        var instances = dataStorageService.getValidInstances(UUID.randomUUID().toString());
        assertThat(instances).isNotNull();
        assertThat(instances.numInstances()).isEqualTo(instancesModel.getInstances().size());
        assertThat(instances.numAttributes()).isEqualTo(instancesModel.getAttributes().size());
    }

    @Test
    void testGetValidInstancesWithServiceUnavailableError() throws IOException {
        FeignException.ServiceUnavailable serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        when(dataStorageLoader.downloadValidInstancesReport(anyString())).thenThrow(serviceUnavailable);
        assertThrows(InternalServiceUnavailableException.class,
                () -> dataStorageService.getValidInstances(UUID.randomUUID().toString()));
    }

    @Test
    void testGetValidInstanceBadRequest() throws IOException {
        var badRequestEx = mock(FeignException.BadRequest.class);
        var validationError = new ValidationErrorDto();
        validationError.setCode(DsInternalApiErrorCode.INSTANCES_NOT_FOUND.getCode());
        when(badRequestEx.contentUTF8())
                .thenReturn(objectMapper.writeValueAsString(Collections.singletonList(validationError)));
        when(dataStorageLoader.downloadValidInstancesReport(anyString())).thenThrow(badRequestEx);
        assertThrows(DataStorageBadRequestException.class,
                () -> dataStorageService.getValidInstances(UUID.randomUUID().toString()));
    }
}
