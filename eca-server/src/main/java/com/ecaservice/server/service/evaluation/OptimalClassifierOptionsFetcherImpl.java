package com.ecaservice.server.service.evaluation;

import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.mapping.ClassifierOptionsRequestMapper;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.server.model.ClassifierOptionsResult;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import com.ecaservice.server.repository.ClassifierOptionsRequestRepository;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import com.ecaservice.server.service.ers.ErsRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.server.util.Utils.createClassifierOptionsRequestEntity;

@Slf4j
@Service("optimalClassifierOptionsFetcherImpl")
@RequiredArgsConstructor
public class OptimalClassifierOptionsFetcherImpl implements OptimalClassifierOptionsFetcher {

    private final CrossValidationConfig crossValidationConfig;
    private final ErsRequestService ersRequestService;
    private final InstancesMetaDataService instancesMetaDataService;
    private final InstancesInfoService instancesInfoService;
    private final ClassifierOptionsRequestMapper classifierOptionsRequestMapper;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    private final ClassifierOptionsRequestRepository classifierOptionsRequestRepository;

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
        var instancesMetaDataModel =
                instancesMetaDataService.getInstancesMetaData(instancesRequestDataModel.getDataUuid());
        var classifierOptionsRequest =
                classifierOptionsRequestMapper.map(instancesRequestDataModel, crossValidationConfig);
        classifierOptionsRequest.setDataHash(instancesMetaDataModel.getMd5Hash());
        ClassifierOptionsRequestEntity requestEntity =
                createClassifierOptionsRequestEntity(ClassifierOptionsRequestSource.ERS);
        requestEntity.setRequestId(classifierOptionsRequest.getRequestId());
        ClassifierOptionsRequestModel requestModel = classifierOptionsRequestModelMapper.map(classifierOptionsRequest);
        var instancesInfo = instancesInfoService.getOrSaveInstancesInfo(instancesMetaDataModel);
        requestModel.setInstancesInfo(instancesInfo);
        ClassifierOptionsResult classifierOptionsResult =
                ersRequestService.getOptimalClassifierOptions(classifierOptionsRequest, requestModel);
        requestEntity.setClassifierOptionsRequestModel(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        log.info("Optimal classifiers options request for data uuid: {}, options req id [{}] has been processed",
                instancesRequestDataModel.getDataUuid(), instancesRequestDataModel.getRequestId());
        return classifierOptionsResult;
    }
}
