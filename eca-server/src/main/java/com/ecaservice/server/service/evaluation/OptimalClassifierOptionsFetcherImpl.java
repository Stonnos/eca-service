package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.mapping.ClassifierOptionsRequestMapper;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.server.model.ClassifierOptionsResult;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.server.service.ers.ErsRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimalClassifierOptionsFetcherImpl implements OptimalClassifierOptionsFetcher {

    private final ErsRequestService ersRequestService;
    private final InstancesInfoService instancesInfoService;
    private final ClassifierOptionsRequestMapper classifierOptionsRequestMapper;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;

    /**
     * Gets optimal classifiers options from ERS service.
     *
     * @param instancesRequestDataModel - instances request data model
     * @return classifier options result
     */
    @Override
    public ClassifierOptionsResult getOptimalClassifierOptions(InstancesRequestDataModel instancesRequestDataModel) {
        log.info("Starting to get optimal classifiers options from ERS for data uuid: {}, options req id [{}]",
                instancesRequestDataModel.getDataUuid(), instancesRequestDataModel.getRequestId());
        var classifierOptionsRequest = classifierOptionsRequestMapper.map(instancesRequestDataModel);
        ClassifierOptionsRequestModel requestModel = classifierOptionsRequestModelMapper.map(classifierOptionsRequest);
        var instancesInfo = instancesInfoService.getOrSaveInstancesInfo(instancesRequestDataModel.getDataUuid());
        requestModel.setInstancesInfo(instancesInfo);
        ClassifierOptionsResult classifierOptionsResult =
                ersRequestService.getOptimalClassifierOptions(classifierOptionsRequest, requestModel);
        log.info("Optimal classifiers options request for data uuid: {}, options req id [{}] has been processed",
                instancesRequestDataModel.getDataUuid(), instancesRequestDataModel.getRequestId());
        return classifierOptionsResult;
    }
}
