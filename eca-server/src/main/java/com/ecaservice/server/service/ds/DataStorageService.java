package com.ecaservice.server.service.ds;

import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.server.exception.DataStorageBadRequestException;
import com.ecaservice.server.exception.DataStorageException;
import com.ecaservice.server.service.DataStorageLoader;
import eca.data.file.converter.InstancesConverter;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.core.Instances;

/**
 * Data storage service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataStorageService {

    private final DataStorageLoader dataStorageLoader;
    private final DataStorageErrorHandler dataStorageErrorHandler;
    private final InstancesConverter instancesConverter = new InstancesConverter();

    /**
     * Gets valid instances with specified uuid.
     *
     * @param uuid - instances uuid
     * @return instances object
     */
    public Instances getValidInstances(String uuid) {
        log.info("Gets valid instances with uuid [{}] from data storage", uuid);
        try {
            var instancesModel = dataStorageLoader.downloadValidInstancesReport(uuid);
            var instances = instancesConverter.convert(instancesModel);
            log.info("Instances [{}] has been fetched from data storage", uuid);
            return instances;
        } catch (FeignException.ServiceUnavailable | RetryableException ex) {
            log.error("Service unavailable error [{}] while get valid instances with uuid [{}] from data storage: {}",
                    ex.getClass().getSimpleName(), uuid, ex.getMessage());
            throw new InternalServiceUnavailableException(
                    String.format("Data storage unavailable while get valid instances with uuid [%s]", uuid));
        } catch (FeignException.BadRequest ex) {
            log.error("Bad request error while get valid instances with uuid [{}] from data storage: {}", uuid,
                    ex.getMessage());
            var dsErrorCode = dataStorageErrorHandler.handleBadRequest(uuid, ex);
            String errorMessage =
                    String.format("Bad request error while get valid instances with uuid [%s] from data storage", uuid);
            throw new DataStorageBadRequestException(dsErrorCode, errorMessage);
        } catch (Exception ex) {
            log.error("Unknown error while get valid instances with uuid [{}] from data storage: {}", uuid,
                    ex.getMessage());
            throw new DataStorageException(
                    String.format("Unknown error while get valid instances with uuid [%s] from data storage", uuid));
        }
    }
}
