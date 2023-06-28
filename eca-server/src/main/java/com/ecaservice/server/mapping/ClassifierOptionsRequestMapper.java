package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Optional;

import static com.ecaservice.server.util.InstancesUtils.md5Hash;

/**
 * Implements mapping to classifier options request.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ClassifierOptionsRequestMapper {

    /**
     * Maps specified params to classifier options request.
     *
     * @param instancesRequest      - instances request
     * @param crossValidationConfig - cross validation config
     * @return classifier options request
     */
    @Mapping(target = "evaluationMethodReport.evaluationMethod", constant = "CROSS_VALIDATION")
    @Mapping(source = "crossValidationConfig.numFolds", target = "evaluationMethodReport.numFolds")
    @Mapping(source = "crossValidationConfig.numTests", target = "evaluationMethodReport.numTests")
    @Mapping(source = "crossValidationConfig.seed", target = "evaluationMethodReport.seed")
    ClassifierOptionsRequest map(InstancesRequestDataModel instancesRequest,
                                 CrossValidationConfig crossValidationConfig);

    /**
     * Maps instances info.
     *
     * @param instancesRequest         - instances request
     * @param classifierOptionsRequest - classifier options request
     */
    @AfterMapping
    default void mapData(InstancesRequestDataModel instancesRequest,
                         @MappingTarget ClassifierOptionsRequest classifierOptionsRequest) {
        if (Optional.ofNullable(instancesRequest.getData()).isPresent()) {
            String dataMd5Hash = md5Hash(instancesRequest.getData());
            classifierOptionsRequest.setDataHash(dataMd5Hash);
        }
    }
}
