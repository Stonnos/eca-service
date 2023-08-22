package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentProcessEntity;
import com.ecaservice.server.model.entity.ExperimentProgressEntity;
import com.ecaservice.server.model.entity.ProcessStatus;
import com.ecaservice.server.repository.ExperimentProcessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Experiment process service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentProcessService {

    private final ExperimentProcessRepository experimentProcessRepository;

    /**
     * Creates new process for experiment.
     *
     * @param experiment - experiment
     */
    public ExperimentProcessEntity createNewProcess(Experiment experiment) {
        log.info("Starting to create new process for experiment [{}]", experiment.getRequestId());
        var experimentProcessEntity = new ExperimentProcessEntity();
        experimentProcessEntity.setExperiment(experiment);
        experimentProcessEntity.setProcessUuid(UUID.randomUUID().toString());
        experimentProcessEntity.setProcessStatus(ProcessStatus.READY);
        experimentProcessEntity.setCreated(LocalDateTime.now());
        experimentProcessRepository.save(experimentProcessEntity);
        log.info("New process [{}] has been created for experiment [{}]", experimentProcessEntity.getProcessUuid(),
                experiment.getRequestId());
        return experimentProcessEntity;
    }

    /**
     * Starts experiment process with specified uuid.
     *
     * @param processUuid - process uuid
     */
    public void startProcess(String processUuid) {
        var experimentProcessEntity = getProcessEntity(processUuid);
        experimentProcessEntity.setProcessStatus(ProcessStatus.IN_PROGRESS);
        experimentProcessEntity.setStarted(LocalDateTime.now());
        experimentProcessRepository.save(experimentProcessEntity);
        log.info("Experiment process [{}] has been started", experimentProcessEntity.getProcessUuid());
    }

    /**
     * Finishes experiment process with specified uuid.
     *
     * @param processUuid - process uuid
     */
    public void completeProcess(String processUuid) {
        var experimentProcessEntity = getProcessEntity(processUuid);
        experimentProcessEntity.setProcessStatus(ProcessStatus.FINISHED);
        experimentProcessEntity.setFinished(LocalDateTime.now());
        experimentProcessRepository.save(experimentProcessEntity);
        log.info("Experiment process [{}] has been finished", experimentProcessEntity.getProcessUuid());
    }

    /**
     * Gets experiment process entity by process uuid.
     *
     * @param processUuid - process uuid
     * @return experiment process entity
     */
    public ExperimentProcessEntity getProcessEntity(String processUuid) {
        log.debug("Gets experiment process entity [{}]", processUuid);
        return experimentProcessRepository.findByProcessUuid(processUuid)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentProgressEntity.class, processUuid));
    }

    /**
     * Gets active processes count for specified experiment.
     *
     * @param experiment - experiment entity
     * @return active processed count
     */
    public long getActiveProcessesCount(Experiment experiment) {
        var activeProcessesCount = experimentProcessRepository.getActiveProcessesCount(experiment);
        log.info("Got [{}] active processes for experiment [{}]", activeProcessesCount, experiment.getRequestId());
        return activeProcessesCount;
    }
}
