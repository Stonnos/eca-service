package com.ecaservice.server.service.data;

import com.ecaservice.common.web.error.WebClientErrorHandler;
import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.data.loader.dto.DataLoaderApiErrorCode;
import com.ecaservice.data.loader.dto.InstancesMetaInfoDto;
import com.ecaservice.server.exception.DataLoaderBadRequestException;
import com.ecaservice.server.exception.DataLoaderException;
import com.ecaservice.server.mapping.InstancesInfoMapper;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import feign.FeignException;
import feign.RetryableException;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Instances meta data service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesMetaDataService {

    private final DataLoaderClient dataLoaderClient;
    private final InstancesInfoMapper instancesInfoMapper;
    private final WebClientErrorHandler webClientErrorHandler = new WebClientErrorHandler();

    /**
     * Gets instances meta data.
     *
     * @param uuid - data uuid
     * @return instances meta data
     */
    @NewSpan
    public InstancesMetaDataModel getInstancesMetaData(String uuid) {
        try {
            log.info("Starting to get instances [{}] meta data", uuid);
            InstancesMetaInfoDto instancesMetaInfoDto = dataLoaderClient.getInstancesMetaInfo(uuid);
            InstancesMetaDataModel instancesMetaDataModel = instancesInfoMapper.map(instancesMetaInfoDto);
            log.info("Instances [{}] meta data has been fetched", uuid);
            return instancesMetaDataModel;
        } catch (FeignException.ServiceUnavailable | RetryableException ex) {
            log.error("Service unavailable error [{}] while get instances [{}] meta data from data storage: {}",
                    ex.getClass().getSimpleName(), uuid, ex.getMessage());
            throw new InternalServiceUnavailableException(
                    String.format("Data loader unavailable while get instances meta data with uuid [%s]", uuid));
        } catch (FeignException.BadRequest ex) {
            log.error("Bad request error while get instances [{}] meta data from data storage: {}", uuid,
                    ex.getMessage());
            var errorCode =
                    webClientErrorHandler.handleBadRequest(uuid, ex.contentUTF8(), DataLoaderApiErrorCode.class);
            log.error("Bad request error code [{}] while get instances [{}] meta data", errorCode, uuid);
            String errorMessage =
                    String.format("Bad request error while get instances [%s] meta data from data storage", uuid);
            throw new DataLoaderBadRequestException(errorCode, errorMessage);
        } catch (Exception ex) {
            log.error("Unknown error while get instances [{}] meta data: {}", uuid, ex.getMessage());
            throw new DataLoaderException(
                    String.format("Unknown error while get instances [%s] meta data", uuid));
        }
    }
}
