package com.ecaservice.ers.service;

import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.mapping.InstancesMapper;
import com.ecaservice.ers.model.InstancesInfo;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
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

    private ConcurrentHashMap<String, Object> cachedDataMd5Hashes = new ConcurrentHashMap<>();

    /**
     * Gets or saves instances info in case if instances with md5 hash doesn't exists.
     *
     * @param evaluationResultsRequest - evaluation results request
     * @return instances info
     */
    public InstancesInfo getOrSaveInstancesInfo(EvaluationResultsRequest evaluationResultsRequest) {
        String xmlData = evaluationResultsRequest.getInstances().getXmlInstances();
        byte[] xmlDataBytes = xmlData.getBytes(StandardCharsets.UTF_8);
        String md5Hash = DigestUtils.md5DigestAsHex(xmlDataBytes);
        InstancesInfo instancesInfo;
        cachedDataMd5Hashes.putIfAbsent(md5Hash, new Object());
        synchronized (cachedDataMd5Hashes.get(md5Hash)) {
            Long instancesInfoId = instancesInfoRepository.findIdByDataMd5Hash(md5Hash);
            if (instancesInfoId != null) {
                instancesInfo = new InstancesInfo();
                instancesInfo.setId(instancesInfoId);
            } else {
                instancesInfo = instancesMapper.map(evaluationResultsRequest.getInstances());
                instancesInfo.setXmlData(xmlDataBytes);
                instancesInfo.setDataMd5Hash(md5Hash);
                instancesInfoRepository.save(instancesInfo);
            }
        }
        cachedDataMd5Hashes.remove(md5Hash);
        return instancesInfo;
    }
}
