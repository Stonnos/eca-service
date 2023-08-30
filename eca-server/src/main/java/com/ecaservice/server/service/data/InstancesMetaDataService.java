package com.ecaservice.server.service.data;

import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.data.loader.dto.DataLoaderApiErrorCode;
import com.ecaservice.data.loader.dto.InstancesMetaInfoDto;
import com.ecaservice.server.exception.DataLoaderBadRequestException;
import com.ecaservice.server.exception.DataLoaderException;
import com.ecaservice.server.mapping.InstancesInfoMapper;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.service.client.FeignClientErrorHandler;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.ecaservice.server.config.cache.CacheNames.INSTANCES_META_DATA_CACHE;

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
    private final FeignClientErrorHandler feignClientErrorHandler;

    /**
     * Gets instances meta data.
     *
     * @param uuid - data uuid
     * @return instances meta data
     */
    @Cacheable(value = INSTANCES_META_DATA_CACHE)
    public InstancesMetaDataModel getInstancesMetaData(String uuid) {
        try {
            log.info("Starting to get instances [{}] meta data", uuid);
            InstancesMetaInfoDto instancesMetaInfoDto = dataLoaderClient.getInstancesMetaInfo(uuid);
            InstancesMetaDataModel instancesMetaDataModel = instancesInfoMapper.map(instancesMetaInfoDto);
            log.info("Instances [{}] meta data has been fetched", uuid);
            return instancesMetaDataModel;
        } catch (FeignException.ServiceUnavailable | RetryableException ex) {
            log.error("Service unavailable error [{}] while get valid instances with uuid [{}] from data storage: {}",
                    ex.getClass().getSimpleName(), uuid, ex.getMessage());
            throw new InternalServiceUnavailableException(
                    String.format("Data loader unavailable while get valid instances meta data with uuid [%s]", uuid));
        } catch (FeignException.BadRequest ex) {
            log.error("Bad request error while get valid instances with uuid [{}] from data storage: {}", uuid,
                    ex.getMessage());
            var errorCode =
                    feignClientErrorHandler.handleBadRequest(uuid, ex, DataLoaderApiErrorCode.class);
            String errorMessage =
                    String.format("Bad request error while get valid instances with uuid [%s] from data storage", uuid);
            throw new DataLoaderBadRequestException(errorCode, errorMessage);
        } catch (Exception ex) {
            log.error("Unknown error while get instances [{}] meta data: {}", uuid, ex.getMessage());
            throw new DataLoaderException(
                    String.format("Unknown error while get instances [%s] meta data", uuid));
        }
    }
}
