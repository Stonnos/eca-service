package com.ecaservice.server.service;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.AttributesInfoEntity;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * Instances saver service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class InstancesProvider {

    private final InstancesMetaDataService instancesMetaDataService;
    private final InstancesInfoRepository instancesInfoRepository;
    private final AttributesInfoRepository attributesInfoRepository;

    /**
     * Gets or save new instances info.
     *
     * @param dataUuid - instances uuid
     * @return instances info
     */
    @Locked(lockName = "getOrSaveInstancesInfo", key = "#dataUuid")
    public InstancesInfo getOrSaveInstancesInfo(String dataUuid) {
        log.info("Gets instances info with uuid [{}]", dataUuid);
        var instancesInfo = instancesInfoRepository.findByUuid(dataUuid);
        if (instancesInfo == null) {
            var instancesMetaDataModel = instancesMetaDataService.getInstancesMetaData(dataUuid);
            instancesInfo = createAndSaveNewInstancesInfo(instancesMetaDataModel);
            log.info("New instances info [{}] has been saved with uuid [{}]", instancesMetaDataModel.getRelationName(),
                    instancesMetaDataModel.getUuid());
        }
        log.info("Instances info with uuid [{}] has been fetched", dataUuid);
        return instancesInfo;
    }

    private InstancesInfo createAndSaveNewInstancesInfo(InstancesMetaDataModel instancesMetaDataModel) {
        var instancesInfo = new InstancesInfo();
        instancesInfo.setRelationName(instancesMetaDataModel.getRelationName());
        instancesInfo.setNumInstances(instancesMetaDataModel.getNumInstances());
        instancesInfo.setNumAttributes(instancesMetaDataModel.getNumAttributes());
        instancesInfo.setNumClasses(instancesMetaDataModel.getNumClasses());
        instancesInfo.setClassName(instancesMetaDataModel.getClassName());
        instancesInfo.setObjectPath(instancesMetaDataModel.getObjectPath());
        instancesInfo.setUuid(instancesMetaDataModel.getUuid());
        instancesInfo.setCreatedDate(LocalDateTime.now());
        instancesInfoRepository.save(instancesInfo);
        createAndSaveAttributes(instancesMetaDataModel, instancesInfo);
        return instancesInfo;
    }

    private void createAndSaveAttributes(InstancesMetaDataModel instancesMetaDataModel,
                                         InstancesInfo instancesInfo) {
        var attributesInfoEntity = new AttributesInfoEntity();
        attributesInfoEntity.setAttributes(instancesMetaDataModel.getAttributes());
        attributesInfoEntity.setInstancesInfo(instancesInfo);
        attributesInfoRepository.save(attributesInfoEntity);
    }
}
