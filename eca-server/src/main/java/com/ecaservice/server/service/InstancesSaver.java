package com.ecaservice.server.service;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.AttributesInfoEntity;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
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
public class InstancesSaver {

    private final InstancesInfoRepository instancesInfoRepository;
    private final AttributesInfoRepository attributesInfoRepository;

    /**
     * Gets or save new instances info.
     *
     * @param instancesMetaDataModel - instances meta data model
     * @return instances info
     */
    @Locked(lockName = "getOrSaveInstancesInfo", key = "#instancesMetaDataModel.uuid")
    public InstancesInfo getOrSaveInstancesInfo(InstancesMetaDataModel instancesMetaDataModel) {
        log.info("Gets instances info [{}] with uuid [{}]", instancesMetaDataModel.getRelationName(),
                instancesMetaDataModel.getUuid());
        var instancesInfo = instancesInfoRepository.findByUuid(instancesMetaDataModel.getUuid());
        if (instancesInfo == null) {
            instancesInfo = createAndSaveNewInstancesInfo(instancesMetaDataModel);
            log.info("New instances info [{}] has been saved with uuid [{}]",
                    instancesMetaDataModel.getRelationName(),
                    instancesMetaDataModel.getUuid());
        }
        log.info("Instances info [{}] with uuid [{}] has been fetched", instancesMetaDataModel.getRelationName(),
                instancesMetaDataModel.getUuid());
        return instancesInfo;
    }

    private InstancesInfo createAndSaveNewInstancesInfo(InstancesMetaDataModel instancesMetaDataModel) {
        var instancesInfo = new InstancesInfo();
        instancesInfo.setRelationName(instancesMetaDataModel.getRelationName());
        instancesInfo.setNumInstances(instancesMetaDataModel.getNumInstances());
        instancesInfo.setNumAttributes(instancesMetaDataModel.getNumAttributes());
        instancesInfo.setNumClasses(instancesMetaDataModel.getNumClasses());
        instancesInfo.setClassName(instancesMetaDataModel.getClassName());
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
