package com.ecaservice.service.ers;

import com.ecaservice.filter.ClassifierOptionsRequestModelFilter;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.util.SortUtils;
import com.ecaservice.web.dto.PageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Implements classifier options request service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ClassifierOptionsRequestService {

    private final ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param classifierOptionsRequestModelRepository - classifier options request model repository bean
     */
    @Inject
    public ClassifierOptionsRequestService(
            ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository) {
        this.classifierOptionsRequestModelRepository = classifierOptionsRequestModelRepository;
    }

    /**
     * Finds classifiers options requests models with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return classifiers options requests models page dto
     */
    public Page<ClassifierOptionsRequestModel> getClassifierOptionsRequestModels(PageRequestDto pageRequestDto) {
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), pageRequestDto.isAscending());
        ClassifierOptionsRequestModelFilter filter =
                new ClassifierOptionsRequestModelFilter(pageRequestDto.getFilters());
        return classifierOptionsRequestModelRepository.findAll(filter,
                PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort));
    }
}
