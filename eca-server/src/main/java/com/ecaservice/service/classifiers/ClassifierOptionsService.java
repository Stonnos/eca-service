package com.ecaservice.service.classifiers;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.repository.ClassifierOptionsDatabaseModelRepository;
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
    private final ClassifierOptionsDatabaseModelRepository classifierOptionsDatabaseModelRepository;

    public void saveClassifierOptions(ClassifierOptions classifierOptions) {

    }

    /**
     * Deletes classifier options with specified id.
     *
     * @param id - classifier options id
     */
    public void deleteOptions(long id) {
        ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel =
                classifierOptionsDatabaseModelRepository.findById(id).orElseThrow(() -> new IllegalStateException(
                        String.format("Classifier options with id [%s] not found!", id)));
        classifierOptionsDatabaseModelRepository.delete(classifierOptionsDatabaseModel);
    }

    @Override
    public Page<ClassifierOptionsDatabaseModel> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return classifierOptionsDatabaseModelRepository.findAll(
                PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }
}
