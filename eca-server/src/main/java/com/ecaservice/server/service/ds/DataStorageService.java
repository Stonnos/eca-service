package com.ecaservice.server.service.ds;

import com.ecaservice.common.web.error.WebClientErrorHandler;
import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.data.storage.dto.DsInternalApiErrorCode;
import com.ecaservice.data.storage.dto.ExportInstancesResponseDto;
import com.ecaservice.server.exception.DataStorageBadRequestException;
import com.ecaservice.server.exception.DataStorageException;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Data storage service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataStorageService {

    private final DataStorageClient dataStorageClient;
    private final WebClientErrorHandler webClientErrorHandler = new WebClientErrorHandler();

    /**
     * Export valid instances with specified uuid to central data storage.
     *
     * @param uuid - instances uuid
     * @return instances object
     */
    public ExportInstancesResponseDto exportValidInstances(String uuid) {
        log.info("Starting to export valid instances [{}] from data editor to central data storage", uuid);
        try {
            var exportInstancesResponseDto = dataStorageClient.exportValidInstances(uuid);
            log.info("Instances [{}] has been exported to central data storage with uuid [{}]",
                    uuid, exportInstancesResponseDto.getExternalDataUuid());
            return exportInstancesResponseDto;
        } catch (FeignException.ServiceUnavailable | RetryableException ex) {
            log.error("Service unavailable error [{}] while export valid instances [{}] to central data storage: {}",
                    ex.getClass().getSimpleName(), uuid, ex.getMessage());
            throw new InternalServiceUnavailableException(
                    String.format("Data storage unavailable while export valid instances [%s]", uuid));
        } catch (FeignException.BadRequest ex) {
            log.error("Bad request error while export valid instances[{}] to central data storage: {}", uuid,
                    ex.getMessage());
            var dsErrorCode =
                    webClientErrorHandler.handleBadRequest(uuid, ex.contentUTF8(), DsInternalApiErrorCode.class);
            String errorMessage =
                    String.format("Bad request error while export valid instances [%s] to central data storage", uuid);
            throw new DataStorageBadRequestException(dsErrorCode, errorMessage);
        } catch (Exception ex) {
            log.error("Unknown error while export valid instances [{}] to central data storage: {}", uuid,
                    ex.getMessage());
            throw new DataStorageException(
                    String.format("Unknown error while export valid instances [%s] to central data storage", uuid));
        }
    }
}
