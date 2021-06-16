package com.ecaservice.service.ers;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.filter.ClassifierOptionsRequestModelFilter;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.service.PageRequestService;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.model.entity.ErsRequest_.REQUEST_DATE;

/**
 * Implements classifier options request service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierOptionsRequestService implements PageRequestService<ClassifierOptionsRequestModel> {

    private final CommonConfig commonConfig;
    private final FilterService filterService;
    private final ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;

    @Override
    public Page<ClassifierOptionsRequestModel> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = buildSort(pageRequestDto.getSortField(), REQUEST_DATE, pageRequestDto.isAscending());
        List<String> globalFilterFields =
                filterService.getGlobalFilterFields(FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST.name());
        ClassifierOptionsRequestModelFilter filter =
                new ClassifierOptionsRequestModelFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                        pageRequestDto.getFilters());
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return classifierOptionsRequestModelRepository.findAll(filter,
                PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }
}
