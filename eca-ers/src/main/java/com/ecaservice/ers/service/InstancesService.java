package com.ecaservice.ers.service;

import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.model.InstancesInfo;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Instances service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesService {

    private final InstancesSaver instancesSaver;
    private final InstancesInfoRepository instancesInfoRepository;

    /**
     * Gets or saves instances info in case if instances with uuid doesn't exists.
     *
     * @param evaluationResultsRequest - evaluation results request
     * @return instances info
     */
    public InstancesInfo getOrSaveInstancesInfo(EvaluationResultsRequest evaluationResultsRequest) {
        String dataUuid = evaluationResultsRequest.getInstances().getUuid();
        // Gets instances or save via double check locking
        InstancesInfo instancesInfo = instancesInfoRepository.findByUuid(dataUuid);
        if (instancesInfo == null) {
            instancesInfo = instancesSaver.getOrSaveInstancesInfo(evaluationResultsRequest);
        }
        return instancesInfo;
    }
}
