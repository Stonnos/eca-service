package com.ecaservice.data.storage.service.data;

import com.ecaservice.common.web.error.WebClientErrorHandler;
import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.common.web.multipart.ByteArrayMultipartFile;
import com.ecaservice.data.loader.dto.DataLoaderApiErrorCode;
import com.ecaservice.data.loader.dto.UploadInstancesResponseDto;
import com.ecaservice.data.storage.exception.DataLoaderBadRequestException;
import com.ecaservice.data.storage.exception.DataLoaderException;
import com.ecaservice.data.storage.service.api.DataLoaderApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.model.InstancesModel;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Upload instances object service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadInstancesObjectService {

    private final DataLoaderApiClient dataLoaderApiClient;
    private final ObjectMapper objectMapper;
    private final WebClientErrorHandler webClientErrorHandler = new WebClientErrorHandler();

    /**
     * Upload instances to central data storage.
     *
     * @param instancesUuid  - instances uuid
     * @param instancesModel - instances bytes array
     * @return upload instances response
     */
    public UploadInstancesResponseDto uploadInstances(String instancesUuid, InstancesModel instancesModel) {
        try {
            log.info("Starting to upload instances [{}] to central data storage", instancesUuid);
            String instancesFilename = String.format("instances-%s.json", instancesUuid);
            byte[] instancesBytes = objectMapper.writeValueAsBytes(instancesModel);
            var multipartFile = new ByteArrayMultipartFile(instancesFilename, instancesFilename, instancesBytes);
            var uploadInstancesResponseDto = dataLoaderApiClient.uploadInstances(multipartFile);
            log.info("Instances [{}] has been uploaded to central data storage", uploadInstancesResponseDto.getUuid());
            return uploadInstancesResponseDto;
        } catch (FeignException.ServiceUnavailable | RetryableException ex) {
            log.error("Service unavailable error [{}] while upload instances [{}] to central data storage: {}",
                    ex.getClass().getSimpleName(), instancesUuid, ex.getMessage());
            throw new InternalServiceUnavailableException(
                    String.format("Data loader unavailable while upload instances [%s] to central data storage",
                            instancesUuid));
        } catch (FeignException.BadRequest ex) {
            log.error("Bad request error while upload instances [{}] to central data storage: {}", instancesUuid,
                    ex.getMessage());
            var errorCode = webClientErrorHandler.handleBadRequest(instancesUuid, ex.contentUTF8(),
                    DataLoaderApiErrorCode.class);
            log.error("Bad request error code [{}] while upload instances [{}] to central data storage", errorCode,
                    instancesUuid);
            String errorMessage = String.format("Bad request error while upload instances [%s] to central data storage",
                    instancesUuid);
            throw new DataLoaderBadRequestException(errorCode, errorMessage);
        } catch (Exception ex) {
            log.error("Unknown error while upload instances [{}] to central data storage: {}", instancesUuid,
                    ex.getMessage());
            throw new DataLoaderException(
                    String.format("Unknown error while upload instances [%s] to central data storage", instancesUuid));
        }
    }
}
