package com.ecaservice.mail.service;

import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.filter.TemplateFilter;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.model.TemplateEntity_;
import com.ecaservice.mail.repository.TemplateRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.mail.model.BaseEntity_.CREATED;

/**
 * Email template service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateService {

    private static final List<String> TEMPLATE_GLOBAL_FILTER_FIELDS = List.of(
            TemplateEntity_.CODE,
            TemplateEntity_.DESCRIPTION,
            TemplateEntity_.SUBJECT
    );

    private final MailConfig mailConfig;
    private final TemplateRepository templateRepository;

    /**
     * Gets the next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @return entities page
     */
    public Page<TemplateEntity> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATED, pageRequestDto.isAscending());
        TemplateFilter filter =
                new TemplateFilter(pageRequestDto.getSearchQuery(), TEMPLATE_GLOBAL_FILTER_FIELDS,
                        pageRequestDto.getFilters());
        int pageSize = Integer.min(pageRequestDto.getSize(), mailConfig.getMaxPageSize());
        return templateRepository.findAll(filter, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }
}
