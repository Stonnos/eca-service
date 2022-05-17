package com.ecaservice.server.service.ers;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.filter.ClassifierOptionsRequestModelFilter;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.server.service.PageRequestService;
import com.ecaservice.server.service.classifiers.ClassifierOptionsProcessor;
import com.ecaservice.web.dto.model.ClassifierInfoDto;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.ErsRequest_.REQUEST_DATE;
import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;

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
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    private final ClassifierOptionsProcessor classifierOptionsProcessor;
    private final FilterService filterService;
    private final ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;

    //TODO added more logs

    @Override
    public Page<ClassifierOptionsRequestModel> getNextPage(PageRequestDto pageRequestDto) {
        log.info("Gets classifiers options requests models page request: {}", pageRequestDto);
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

    public PageDto<ClassifierOptionsRequestDto> getClassifierOptionsRequestModels(PageRequestDto pageRequestDto) {
        var classifierOptionsRequestModelPage = getNextPage(pageRequestDto);
        var classifierOptionsRequestDtoList = classifierOptionsRequestModelPage.getContent()
                .stream()
                .map(classifierOptionsRequestModel -> {
                    var classifierOptionsRequestDto =
                            classifierOptionsRequestModelMapper.map(classifierOptionsRequestModel);
                    var classifierInfoDto = populateOptimalClassifierInfo(classifierOptionsRequestModel);
                    classifierOptionsRequestDto.setOptimalClassifierInfo(classifierInfoDto);
                    return classifierOptionsRequestDto;
                })
                .collect(Collectors.toList());
        return PageDto.of(classifierOptionsRequestDtoList, pageRequestDto.getPage(),
                classifierOptionsRequestModelPage.getTotalElements());
    }

    private ClassifierInfoDto populateOptimalClassifierInfo(
            ClassifierOptionsRequestModel classifierOptionsRequestModel) {
        var classifierOptionsResponseModel =
                classifierOptionsRequestModel.getClassifierOptionsResponseModels().iterator().next();
        var classifierOptions = parseOptions(classifierOptionsResponseModel.getOptions());
        return classifierOptionsProcessor.processClassifierInfo(classifierOptions);
    }
}
