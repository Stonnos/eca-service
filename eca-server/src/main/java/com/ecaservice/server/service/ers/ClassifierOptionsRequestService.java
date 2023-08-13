package com.ecaservice.server.service.ers;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.server.filter.ClassifierOptionsRequestModelFilter;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.server.service.classifiers.ClassifierOptionsProcessor;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.ErsRequest_.REQUEST_DATE;
import static com.ecaservice.server.model.entity.FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST;
import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;

/**
 * Implements classifier options request service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class ClassifierOptionsRequestService {

    private final FilterService filterService;
    private final ClassifierOptionsProcessor classifierOptionsProcessor;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    private final ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;

    /**
     * Gets next classifier options requests models page.
     *
     * @param pageRequestDto - page request dto
     * @return classifier options requests models page
     */
    public Page<ClassifierOptionsRequestModel> getNextPage(
            @ValidPageRequest(filterTemplateName = CLASSIFIER_OPTIONS_REQUEST) PageRequestDto pageRequestDto) {
        log.info("Gets classifier options requests next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortField(), REQUEST_DATE, pageRequestDto.isAscending());
        var globalFilterFields = filterService.getGlobalFilterFields(CLASSIFIER_OPTIONS_REQUEST);
        ClassifierOptionsRequestModelFilter filter =
                new ClassifierOptionsRequestModelFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                        pageRequestDto.getFilters());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var classifierOptionsRequestsPage =
                classifierOptionsRequestModelRepository.findAll(filter, pageRequest);
        log.info("Classifier options requests page [{} of {}] with size [{}] has been fetched for page request [{}]",
                classifierOptionsRequestsPage.getNumber(), classifierOptionsRequestsPage.getTotalPages(),
                classifierOptionsRequestsPage.getNumberOfElements(), pageRequestDto);
        return classifierOptionsRequestsPage;
    }

    /**
     * Gets classifier options requests dto page.
     *
     * @param pageRequestDto - page request dto
     * @return classifier options requests dto page
     */
    public PageDto<ClassifierOptionsRequestDto> getClassifierOptionsRequestsPage(PageRequestDto pageRequestDto) {
        var classifierOptionsRequestsPage = getNextPage(pageRequestDto);
        var classifierOptionsRequestsDtoPage = classifierOptionsRequestsPage.getContent()
                .stream()
                .map(this::processClassifierOptionsRequest)
                .collect(Collectors.toList());
        return PageDto.of(classifierOptionsRequestsDtoPage, pageRequestDto.getPage(),
                classifierOptionsRequestsPage.getTotalElements());
    }

    private ClassifierOptionsRequestDto processClassifierOptionsRequest(
            ClassifierOptionsRequestModel classifierOptionsRequestModel) {
        var classifierOptionsRequestDto =
                classifierOptionsRequestModelMapper.map(classifierOptionsRequestModel);
        if (ErsResponseStatus.SUCCESS.equals(classifierOptionsRequestModel.getResponseStatus()) &&
                !CollectionUtils.isEmpty(classifierOptionsRequestModel.getClassifierOptionsResponseModels())) {
            var classifierOptionsResponseModel =
                    classifierOptionsRequestModel.getClassifierOptionsResponseModels().iterator().next();
            var classifierOptions = parseOptions(classifierOptionsResponseModel.getOptions());
            var classifierInfoDto = classifierOptionsProcessor.processClassifierOptions(classifierOptions);
            classifierOptionsRequestDto.setClassifierInfo(classifierInfoDto);
        }
        return classifierOptionsRequestDto;
    }
}
