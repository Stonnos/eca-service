package com.ecaservice.server.service.ers;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.filter.ClassifierOptionsRequestModelFilter;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.server.service.PageRequestService;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.ErsRequest_.REQUEST_DATE;

/**
 * Implements classifier options request service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierOptionsRequestService implements PageRequestService<ClassifierOptionsRequestModel> {

    private final AppProperties appProperties;
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
        int pageSize = Integer.min(pageRequestDto.getSize(), appProperties.getMaxPageSize());
        return classifierOptionsRequestModelRepository.findAll(filter,
                PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }
}
