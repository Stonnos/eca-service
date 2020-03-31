package com.ecaservice.service.classifiers;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.exception.EntityNotFoundException;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifiersConfiguration;
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
    public void saveClassifierOptions(long configurationId, ClassifierOptions classifierOptions) {
        ClassifiersConfiguration classifiersConfiguration =
                classifiersConfigurationRepository.findById(configurationId).orElseThrow(
                        () -> new EntityNotFoundException(ClassifiersConfiguration.class, configurationId));
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                createClassifierOptionsDatabaseModel(classifierOptions, classifiersConfiguration);
        classifierOptionsDatabaseModelRepository.save(classifierOptionsDatabaseModel);
        log.info("New classifier options has been saved for configuration [{}]", configurationId);
    }

    /**
     * Deletes classifier options with specified id.
     *
     * @param id - classifier options id
     */
    public void deleteOptions(long id) {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                classifierOptionsDatabaseModelRepository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException(ClassifierOptionsDatabaseModel.class, id));
        classifierOptionsDatabaseModelRepository.delete(classifierOptionsDatabaseModel);
        log.info("Classifier options with id [{}] has been deleted", classifierOptionsDatabaseModel.getId());
    }

    @Override
    public Page<ClassifierOptionsDatabaseModel> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return classifierOptionsDatabaseModelRepository.findAll(
                PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }
}
