package com.ecaservice.server.service.experiment;

import com.ecaservice.server.model.entity.ExperimentStepEntity;
import com.ecaservice.server.model.entity.ExperimentStepStatus;
import com.ecaservice.server.repository.ExperimentStepRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentStepService {

    private final ExperimentStepRepository experimentStepRepository;

    public void complete(ExperimentStepEntity experimentStepEntity) {
        experimentStepEntity.setStatus(ExperimentStepStatus.COMPLETED);
        experimentStepRepository.save(experimentStepEntity);
    }

    public void completeWithError(ExperimentStepEntity experimentStepEntity, String errorMessage) {
        experimentStepEntity.setStatus(ExperimentStepStatus.ERROR);
        experimentStepEntity.setErrorMessage(errorMessage);
        experimentStepRepository.save(experimentStepEntity);
    }

    public void failed(ExperimentStepEntity experimentStepEntity, String errorMessage) {
        experimentStepEntity.setStatus(ExperimentStepStatus.FAILED);
        experimentStepEntity.setErrorMessage(errorMessage);
        experimentStepRepository.save(experimentStepEntity);
    }
}
