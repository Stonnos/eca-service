package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.repository.ExportInstancesObjectRepository;
import com.ecaservice.web.dto.model.RoutePathDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.data.storage.util.RoutePaths.INSTANCES_DETAILS_PATH;

/**
 * Instances path service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesPathService {

    private final ExportInstancesObjectRepository exportInstancesObjectRepository;

    /**
     * Gets instances path by external data uuid from central storage.
     *
     * @param externalDataUuid - external data uuid
     * @return instances route path
     */
    public RoutePathDto getInstancesPath(String externalDataUuid) {
        log.info("Gets instances route path by external data uuid [{}]", externalDataUuid);
        RoutePathDto routePathDto = new RoutePathDto();
        var exportInstancesObjectEntity = exportInstancesObjectRepository.findFirstByExternalDataUuid(externalDataUuid);
        if (exportInstancesObjectEntity == null) {
            log.warn("Can't find instances route path by external data uuid [{}]", externalDataUuid);
        } else {
            InstancesEntity instancesEntity = exportInstancesObjectEntity.getInstancesEntity();
            routePathDto.setPath(String.format(INSTANCES_DETAILS_PATH,instancesEntity.getId()));
        }
        return routePathDto;
    }
}
