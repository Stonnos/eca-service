package com.ecaservice.ers.service;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.exception.ResultsNotFoundException;
import com.ecaservice.ers.mapping.ClassifierOptionsInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Service for handling classifier options requests.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierOptionsRequestService {

    private final ClassifierOptionsService classifierOptionsService;
    private final ClassifierOptionsInfoMapper classifierOptionsInfoMapper;

    /**
     * Finds optimal classifiers options.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options response
     */
    public ClassifierOptionsResponse findClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        log.info("Starting to find optimal classifiers options with request id [{}] for data md5 hash [{}]",
                classifierOptionsRequest.getRequestId(), classifierOptionsRequest.getDataHash());
        var classifierOptionsInfoList = classifierOptionsService.findBestClassifierOptions(classifierOptionsRequest);
        if (CollectionUtils.isEmpty(classifierOptionsInfoList)) {
            throw new ResultsNotFoundException(
                    String.format("Best classifiers options not found for data md5 hash [%s], request id [%s]",
                            classifierOptionsRequest.getDataHash(), classifierOptionsRequest.getRequestId()));
        } else {
            log.info("[{}] best classifiers options has been found for data md5 hash [{}], request id [{}]",
                    classifierOptionsInfoList.size(), classifierOptionsRequest.getDataHash(),
                    classifierOptionsRequest.getRequestId());
            var classifierOptionsReports = classifierOptionsInfoMapper.map(classifierOptionsInfoList);
            return ClassifierOptionsResponse.builder()
                    .requestId(classifierOptionsRequest.getRequestId())
                    .classifierReports(classifierOptionsReports)
                    .build();
        }
    }
}
