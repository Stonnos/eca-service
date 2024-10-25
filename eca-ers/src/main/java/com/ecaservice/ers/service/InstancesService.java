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
    @Locked(lockName = "getOrSaveErsInstancesInfo", key = "#evaluationResultsRequest.instances.uuid")
    public InstancesInfo getOrSaveInstancesInfo(EvaluationResultsRequest evaluationResultsRequest) {
        String dataUuid = evaluationResultsRequest.getInstances().getUuid();
        log.info("Starting to get instances [{}] with uuid [{}]",
                evaluationResultsRequest.getInstances().getRelationName(), dataUuid);
        InstancesInfo instancesInfo;
        instancesInfo = instancesInfoRepository.findByUuid(dataUuid);
        if (instancesInfo == null) {
            instancesInfo = instancesMapper.map(evaluationResultsRequest.getInstances());
            instancesInfo.setCreatedDate(LocalDateTime.now());
            instancesInfoRepository.save(instancesInfo);
            log.info("New instances [{}] with uuid [{}] has been saved",
                    evaluationResultsRequest.getInstances().getRelationName(), dataUuid);
        }
        log.info("Instances [{}] with uuid [{}] has been fetched",
                evaluationResultsRequest.getInstances().getRelationName(), dataUuid);
        return instancesInfo;
    }
}
