package com.ecaservice.service.classifiers;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.exception.EntityNotFoundException;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.entity.ClassifiersConfigurationSource;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
import com.ecaservice.repository.ClassifiersConfigurationRepository;
import com.ecaservice.service.PageRequestService;
import com.ecaservice.util.SortUtils;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.model.entity.ClassifierOptionsDatabaseModel_.CREATION_DATE;
import static com.ecaservice.util.ClassifierOptionsHelper.createClassifierOptionsDatabaseModel;

/**
 * Classifier options service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierOptionsService implements PageRequestService<ClassifierOptionsDatabaseModel> {

    private final CommonConfig commonConfig;
    private final ClassifiersConfigurationRepository classifiersConfigurationRepository;
    private final ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;

    /**
     * Saves new classifier options for specified configuration.
     *
     * @param configurationId   - configuration id
     * @param classifierOptions - classifier options
     */
    @Transactional
    public void saveClassifierOptions(long configurationId, ClassifierOptions classifierOptions) {
        ClassifiersConfiguration classifiersConfiguration =
                classifiersConfigurationRepository.findById(configurationId).orElseThrow(
                        () -> new EntityNotFoundException(ClassifiersConfiguration.class, configurationId));
        Assert.state(!ClassifiersConfigurationSource.SYSTEM.equals(classifiersConfiguration.getSource()),
                "Can't add classifier options to system configuration!");
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                createClassifierOptionsDatabaseModel(classifierOptions, classifiersConfiguration);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        classifierOptionsDatabaseModelRepository.save(classifierOptionsDatabaseModel);
        classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("New classifier options has been saved for configuration [{}]", configurationId);
    }

    /**
     * Deletes classifier options with specified id.
     *
     * @param id - classifier options id
     */
    @Transactional
    public void deleteOptions(long id) {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                classifierOptionsDatabaseModelRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException(ClassifierOptionsDatabaseModel.class, id));
        ClassifiersConfiguration classifiersConfiguration = classifierOptionsDatabaseModel.getConfiguration();
        Assert.state(!ClassifiersConfigurationSource.SYSTEM.equals(classifiersConfiguration.getSource()),
                "Can't delete classifier options from system configuration!");
        classifierOptionsDatabaseModelRepository.delete(classifierOptionsDatabaseModel);
        classifiersConfiguration.setUpdated(LocalDateTime.now());
        classifiersConfigurationRepository.save(classifiersConfiguration);
        log.info("Classifier options with id [{}] has been deleted", classifierOptionsDatabaseModel.getId());
    }

    @Override
    public Page<ClassifierOptionsDatabaseModel> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return classifierOptionsDatabaseModelRepository.findAll(
                PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }

    /**
     * Finds classifiers options for active configuration.
     *
     * @return classifiers options list
     */
    public List<ClassifierOptionsDatabaseModel> getActiveClassifiersOptions() {
        ClassifiersConfiguration activeConfiguration =
                classifiersConfigurationRepository.findFirstByActiveTrue().orElseThrow(
                        () -> new IllegalStateException("Can't find active classifiers configuration!"));
        return classifierOptionsDatabaseModelRepository.findAllByConfiguration(activeConfiguration);
    }
}
