package com.ecaservice.server.service.experiment;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.server.dto.CreateExperimentRequestDto;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.event.model.push.ExperimentWebPushEvent;
import com.ecaservice.server.exception.DataStorageBadRequestException;
import com.ecaservice.server.exception.InstancesValidationException;
import com.ecaservice.server.mapping.DataStorageErrorCodeMapper;
import com.ecaservice.server.model.MsgProperties;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.service.UserService;
import com.ecaservice.server.service.auth.UsersClient;
import com.ecaservice.server.service.ds.DataStorageService;
import com.ecaservice.user.dto.UserInfoDto;
import com.ecaservice.web.dto.model.CreateExperimentResultDto;
import eca.filter.ConstantAttributesFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import static com.ecaservice.server.config.audit.AuditCodes.CREATE_EXPERIMENT_REQUEST;
import static com.ecaservice.server.util.InstancesUtils.removeConstantAttributes;

/**
 * Experiment request web api service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentRequestWebApiService {

    private final UserService userService;
    private final UsersClient usersClient;
    private final DataStorageService dataStorageService;
    private final ExperimentService experimentService;
    private final DataStorageErrorCodeMapper dataStorageErrorCodeMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Creates experiment request.
     *
     * @param experimentRequestDto - experiment request dto
     * @return created experiment result
     */
    @Audit(value = CREATE_EXPERIMENT_REQUEST, correlationIdKey = "#result.requestId")
    public CreateExperimentResultDto createExperiment(CreateExperimentRequestDto experimentRequestDto) {
        log.info("Starting to create experiment request for instances uuid [{}], type [{}], evaluation method [{}]",
                experimentRequestDto.getInstancesUuid(), experimentRequestDto.getExperimentType(),
                experimentRequestDto.getEvaluationMethod());
        var experimentRequest = createExperimentRequest(experimentRequestDto);
        var msgProperties = MsgProperties.builder()
                .channel(Channel.WEB)
                .build();
        var experiment = experimentService.createExperiment(experimentRequest, msgProperties);
        eventPublisher.publishEvent(new ExperimentWebPushEvent(this, experiment));
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
        log.info("Experiment request [{}] has been created for instances uuid [{}].", experiment.getRequestId(),
                experimentRequestDto.getInstancesUuid());
        return CreateExperimentResultDto.builder()
                .id(experiment.getId())
                .requestId(experiment.getRequestId())
                .build();
    }

    private ExperimentRequest createExperimentRequest(CreateExperimentRequestDto experimentRequestDto) {
        log.info("Starting to get current user info for experiment request [{}]", experimentRequestDto);
        var userInfoDto = getCurrentUserInfo();
        log.info("User info has been fetched for experiment request [{}]", experimentRequestDto);
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setEmail(userInfoDto.getEmail());
        Instances data = downloadInstances(experimentRequestDto.getInstancesUuid());
        Instances filteredData = removeConstantAttributes(data);
        log.info("Constant attributes has been removed from data [{}]", filteredData.relationName());
        experimentRequest.setData(filteredData);
        experimentRequest.setExperimentType(experimentRequestDto.getExperimentType());
        experimentRequest.setEvaluationMethod(experimentRequestDto.getEvaluationMethod());
        return experimentRequest;
    }

    private UserInfoDto getCurrentUserInfo() {
        var user = userService.getCurrentUser();
        return usersClient.getUserInfo(user);
    }

    private Instances downloadInstances(String uuid) {
        try {
            return dataStorageService.getValidInstances(uuid);
        } catch (DataStorageBadRequestException ex) {
            var dsErrorCode = dataStorageErrorCodeMapper.mapErrorCode(ex.getDsErrorCode());
            throw new InstancesValidationException(dsErrorCode, ex.getMessage());
        }
    }
}