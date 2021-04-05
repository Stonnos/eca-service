package com.ecaservice.ers.service;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.ResponseStatus;
import com.ecaservice.ers.exception.DataNotFoundException;
import com.ecaservice.ers.mapping.ClassifierOptionsInfoMapper;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import com.ecaservice.ers.util.Utils;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

import static com.ecaservice.ers.config.MetricConstants.GET_OPTIMAL_CLASSIFIER_OPTIONS_TIMED_METRIC_NAME;

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
    @Timed(value = GET_OPTIMAL_CLASSIFIER_OPTIONS_TIMED_METRIC_NAME)
    public ClassifierOptionsResponse findClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        String requestId = UUID.randomUUID().toString();
        log.info("Received request [{}] for searching optimal classifiers options.", requestId);
        ResponseStatus responseStatus = ResponseStatus.SUCCESS;
        try {
            log.info("Starting to find optimal classifiers options with request id [{}] for data '{}' classification.",
                    requestId, classifierOptionsRequest.getRelationName());
            List<ClassifierOptionsInfo> classifierOptionsInfoList =
                    classifierOptionsService.findBestClassifierOptions(classifierOptionsRequest);
            if (CollectionUtils.isEmpty(classifierOptionsInfoList)) {
                log.info("Best classifiers options not found for data '{}', request id [{}]",
                        classifierOptionsRequest.getRelationName(), requestId);
                responseStatus = ResponseStatus.RESULTS_NOT_FOUND;
            } else {
                log.info("{} best classifiers options has been found for data '{}', request id [{}]",
                        classifierOptionsInfoList.size(), classifierOptionsRequest.getRelationName(), requestId);
                return Utils.buildClassifierOptionsResponse(requestId,
                        classifierOptionsInfoMapper.map(classifierOptionsInfoList), responseStatus);
            }
        } catch (DataNotFoundException ex) {
            log.warn(ex.getMessage());
            responseStatus = ResponseStatus.DATA_NOT_FOUND;
        }
        return Utils.buildClassifierOptionsResponse(requestId, responseStatus);
    }
}
