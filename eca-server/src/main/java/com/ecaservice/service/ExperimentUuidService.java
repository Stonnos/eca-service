package com.ecaservice.service;

import com.ecaservice.exception.EcaServiceException;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

/**
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ExperimentUuidService {

    private final ExperimentRepository experimentRepository;

    @Inject
    public ExperimentUuidService(ExperimentRepository experimentRepository) {
        this.experimentRepository = experimentRepository;
    }

    @PostConstruct
    public void perform() {
        List<Experiment> experiments = experimentRepository.findAllByUuidIsNull();
        log.info("Found {} experiments with null uuid", experiments.size());
        if (!CollectionUtils.isEmpty(experiments)) {
            experiments.forEach(experiment -> {
                String uuid = UUID.randomUUID().toString();
                if (experimentRepository.existsByUuid(uuid)) {
                    throw new EcaServiceException(String.format("Experiment with uuid [%s] is already exists", uuid));
                }
                experiment.setUuid(uuid);
                experimentRepository.save(experiment);
            });
        }
    }
}
