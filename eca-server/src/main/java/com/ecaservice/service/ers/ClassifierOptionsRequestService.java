package com.ecaservice.service.ers;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.filter.ClassifierOptionsRequestModelFilter;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.service.PageRequestService;
import com.ecaservice.service.filter.GlobalFilterService;
import com.ecaservice.util.SortUtils;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Implements classifier options request service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ClassifierOptionsRequestService implements PageRequestService<ClassifierOptionsRequestModel> {

    private final CommonConfig commonConfig;
    private final GlobalFilterService globalFilterService;
    private final ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param commonConfig                            - common config bean
     * @param globalFilterService                     - global filter service bean
     * @param classifierOptionsRequestModelRepository - classifier options request model repository bean
     */
    @Inject
    public ClassifierOptionsRequestService(
            CommonConfig commonConfig,
            GlobalFilterService globalFilterService,
            ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository) {
        this.commonConfig = commonConfig;
        this.globalFilterService = globalFilterService;
        this.classifierOptionsRequestModelRepository = classifierOptionsRequestModelRepository;
    }

    @Override
    public Page<ClassifierOptionsRequestModel> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = SortUtils.buildSort(pageRequestDto.getSortField(), pageRequestDto.isAscending());
        List<String> globalFilterFields =
                globalFilterService.getGlobalFilterFields(FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST);
        ClassifierOptionsRequestModelFilter filter =
                new ClassifierOptionsRequestModelFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                        pageRequestDto.getFilters());
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return classifierOptionsRequestModelRepository.findAll(filter,
                PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }
}
