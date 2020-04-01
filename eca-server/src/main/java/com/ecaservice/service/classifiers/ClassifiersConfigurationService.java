package com.ecaservice.service.classifiers;

import com.ecaservice.exception.EntityNotFoundException;
import com.ecaservice.mapping.ClassifiersConfigurationMapper;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Classifiers configuration service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifiersConfigurationService {

    private final ClassifiersConfigurationMapper classifiersConfigurationMapper;
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;

    /**
     * Saves new classifiers configuration.
     *
     * @param configurationDto - create classifiers configuration dto
     */
    public void save(CreateClassifiersConfigurationDto configurationDto) {
        ClassifiersConfiguration classifiersConfiguration = classifiersConfigurationMapper.map(configurationDto);
        classifiersConfiguration.setCreated(LocalDateTime.now());
        classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("Classifiers configuration [{}] has been saved", classifiersConfiguration.getName());
    }

    /**
     * Updates classifiers configuration.
     *
     * @param configurationDto - update classifiers configuration dto
     */
    public void update(UpdateClassifiersConfigurationDto configurationDto) {
        ClassifiersConfiguration classifiersConfiguration = getById(configurationDto.getId());
        classifiersConfigurationMapper.update(configurationDto, classifiersConfiguration);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("Classifiers configuration [{}] has been updated", classifiersConfiguration.getId());
    }

    /**
     * Deletes classifiers configuration by specified id.
     *
     * @param id - classifiers configuration id
     */
    public void delete(long id) {
        ClassifiersConfiguration classifiersConfiguration = getById(id);
        classifiersConfigurationRepository.delete(classifiersConfiguration);
        log.info("Classifiers configuration [{}] has been deleted", id);
    }

    /**
     * Sets classifiers configuration as active.
     *
     * @param id - configuration id
     */
    public void setActive(long id) {
        ClassifiersConfiguration classifiersConfiguration = getById(id);
        ClassifiersConfiguration activeConfiguration =
                classifiersConfigurationRepository.findFirstByActiveTrue().orElseThrow(
                        () -> new IllegalStateException("Can't find active classifiers configuration!"));
        if (!classifiersConfiguration.getId().equals(activeConfiguration.getId())) {
            activeConfiguration.setActive(false);
            classifiersConfiguration.setActive(true);
            classifiersConfigurationRepository.saveAll(Arrays.asList(classifiersConfiguration, activeConfiguration));
            log.info("Classifiers configuration [{}] has been set as active.", classifiersConfiguration.getId());
        }
    }

    private ClassifiersConfiguration getById(long id) {
        return classifiersConfigurationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ClassifiersConfiguration.class, id));
    }
}
