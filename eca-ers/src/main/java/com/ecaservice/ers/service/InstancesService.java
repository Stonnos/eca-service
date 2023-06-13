package com.ecaservice.ers.service;

import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.mapping.InstancesMapper;
import com.ecaservice.ers.model.InstancesInfo;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

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

    private final ConcurrentHashMap<String, Object> cachedDataMd5Hashes = new ConcurrentHashMap<>();

    /**
     * Gets or saves instances info in case if instances with md5 hash doesn't exists.
     *
     * @param evaluationResultsRequest - evaluation results request
     * @return instances info
     */
    public InstancesInfo getOrSaveInstancesInfo(EvaluationResultsRequest evaluationResultsRequest) {
        String dataMd5Hash = evaluationResultsRequest.getInstances().getDataMd5Hash();
        log.info("Starting to get instances [{}] with md5 hash [{}]",
                evaluationResultsRequest.getInstances().getRelationName(), dataMd5Hash);
        InstancesInfo instancesInfo;
        cachedDataMd5Hashes.putIfAbsent(dataMd5Hash, new Object());
        synchronized (cachedDataMd5Hashes.get(dataMd5Hash)) {
            instancesInfo = instancesInfoRepository.findByDataMd5Hash(dataMd5Hash);
            if (instancesInfo == null) {
                instancesInfo = instancesMapper.map(evaluationResultsRequest.getInstances());
                instancesInfoRepository.save(instancesInfo);
                log.info("New instances [{}] with md5 hash [{}] has been saved",
                        evaluationResultsRequest.getInstances().getRelationName(), dataMd5Hash);
            }
        }
        cachedDataMd5Hashes.remove(dataMd5Hash);
        log.info("Instances [{}] with md5 hash [{}] has been fetched",
                evaluationResultsRequest.getInstances().getRelationName(), dataMd5Hash);
        return instancesInfo;
    }
}
