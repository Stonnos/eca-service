package com.ecaservice.server.service;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.repository.InstancesInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Instances info service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesInfoService {

    public final InstancesInfoRepository instancesInfoRepository;

    /**
     * Gets or save new instances info.
     *
     * @param dataMd5Hash - data md5 hash
     * @param data        - instances object
     * @return instances info
     */
    @Locked(lockName = "getOrSaveInstancesInfo", key = "#dataMd5Hash")
    public InstancesInfo getOrSaveInstancesInfo(String dataMd5Hash, Instances data) {
        log.info("Gets instances info [{}] with md5 hash [{}]", data.relationName(), dataMd5Hash);
        var instancesInfo = instancesInfoRepository.findByDataMd5Hash(dataMd5Hash);
        if (instancesInfo == null) {
            instancesInfo = new InstancesInfo();
            instancesInfo.setRelationName(data.relationName());
            instancesInfo.setNumInstances(data.numInstances());
            instancesInfo.setNumAttributes(data.numAttributes());
            instancesInfo.setNumClasses(data.numClasses());
            instancesInfo.setClassName(data.classAttribute().name());
            instancesInfo.setDataMd5Hash(dataMd5Hash);
            instancesInfo.setUuid(UUID.randomUUID().toString());
            instancesInfo.setCreatedDate(LocalDateTime.now());
            instancesInfoRepository.save(instancesInfo);
            log.info("New instances info [{}] has been saved with md5 hash [{}]", data.relationName(), dataMd5Hash);
        }
        log.info("Instances info [{}] with md5 hash [{}] has been fetched", data.relationName(), dataMd5Hash);
        return instancesInfo;
    }
}
