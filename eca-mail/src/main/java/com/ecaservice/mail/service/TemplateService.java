package com.ecaservice.mail.service;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.mail.filter.TemplateFilter;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.repository.TemplateRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.mail.dictionary.FilterDictionaries.EMAIL_TEMPLATES;
import static com.ecaservice.mail.model.BaseEntity_.CREATED;

/**
 * Email template service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class TemplateService {

    private final FilterTemplateService filterTemplateService;
    private final TemplateRepository templateRepository;

    /**
     * Gets the next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @return entities page
     */
    public Page<TemplateEntity> getNextPage(
            @ValidPageRequest(filterTemplateName = EMAIL_TEMPLATES) PageRequestDto pageRequestDto) {
        log.info("Gets email templates next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATED, pageRequestDto.isAscending());
        var globalFilterFields = filterTemplateService.getGlobalFilterFields(EMAIL_TEMPLATES);
        TemplateFilter filter =
                new TemplateFilter(pageRequestDto.getSearchQuery(), globalFilterFields, pageRequestDto.getFilters());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var templatesPage = templateRepository.findAll(filter, pageRequest);
        log.info("Email templates page [{} of {}] with size [{}] has been fetched for page request [{}]",
                templatesPage.getNumber(), templatesPage.getTotalPages(), templatesPage.getNumberOfElements(),
                pageRequestDto);
        return templatesPage;
    }
}
