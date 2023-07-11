package com.ecaservice.ers.service;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.mapping.InstancesMapper;
import com.ecaservice.ers.model.InstancesInfo;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Instances service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesService {

    private final InstancesMapper instancesMapper;
    private final InstancesInfoRepository instancesInfoRepository;

    /**
     * Gets or saves instances info in case if instances with md5 hash doesn't exists.
     *
     * @param evaluationResultsRequest - evaluation results request
     * @return instances info
     */
    @Locked(lockName = "getOrSaveErsInstancesInfo", key = "#evaluationResultsRequest.instances.dataMd5Hash")
    public InstancesInfo getOrSaveInstancesInfo(EvaluationResultsRequest evaluationResultsRequest) {
        String dataMd5Hash = evaluationResultsRequest.getInstances().getDataMd5Hash();
        log.info("Starting to get instances [{}] with md5 hash [{}]",
                evaluationResultsRequest.getInstances().getRelationName(), dataMd5Hash);
        InstancesInfo instancesInfo;
        instancesInfo = instancesInfoRepository.findByDataMd5Hash(dataMd5Hash);
        if (instancesInfo == null) {
            instancesInfo = instancesMapper.map(evaluationResultsRequest.getInstances());
            instancesInfo.setCreatedDate(LocalDateTime.now());
            instancesInfoRepository.save(instancesInfo);
            log.info("New instances [{}] with md5 hash [{}] has been saved",
                    evaluationResultsRequest.getInstances().getRelationName(), dataMd5Hash);
        }
        log.info("Instances [{}] with md5 hash [{}] has been fetched",
                evaluationResultsRequest.getInstances().getRelationName(), dataMd5Hash);
        return instancesInfo;
    }
}
