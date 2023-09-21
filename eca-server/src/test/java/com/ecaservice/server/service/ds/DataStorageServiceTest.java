package com.ecaservice.server.service.ds;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.data.storage.dto.DsInternalApiErrorCode;
import com.ecaservice.data.storage.dto.ExportInstancesResponseDto;
import com.ecaservice.server.exception.DataStorageBadRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@Import(DataStorageService.class)
class DataStorageServiceTest {

    @MockBean
    private DataStorageClient dataStorageClient;

    @Inject
    private DataStorageService dataStorageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testExportValidInstances() {
        ExportInstancesResponseDto exportInstancesResponseDto = ExportInstancesResponseDto.builder()
                .externalDataUuid(UUID.randomUUID().toString())
                .build();
        when(dataStorageClient.exportValidInstances(anyString())).thenReturn(exportInstancesResponseDto);
        var result = dataStorageService.exportValidInstances(UUID.randomUUID().toString());
        assertThat(result).isNotNull();
    }

    @Test
    void testExportValidInstancesWithServiceUnavailableError() throws IOException {
        FeignException.ServiceUnavailable serviceUnavailable = mock(FeignException.ServiceUnavailable.class);
        when(dataStorageClient.exportValidInstances(anyString())).thenThrow(serviceUnavailable);
        assertThrows(InternalServiceUnavailableException.class,
                () -> dataStorageService.exportValidInstances(UUID.randomUUID().toString()));
    }

    @Test
    void testExportValidInstanceBadRequest() throws IOException {
        var badRequestEx = mock(FeignException.BadRequest.class);
        var validationError = new ValidationErrorDto();
        validationError.setCode(DsInternalApiErrorCode.INSTANCES_NOT_FOUND.getCode());
        when(badRequestEx.contentUTF8())
                .thenReturn(objectMapper.writeValueAsString(Collections.singletonList(validationError)));
        when(dataStorageClient.exportValidInstances(anyString())).thenThrow(badRequestEx);
        assertThrows(DataStorageBadRequestException.class,
                () -> dataStorageService.exportValidInstances(UUID.randomUUID().toString()));
    }
}
