package com.ecaservice.report.data.fetcher;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.report.data.customize.FilterValueReportCustomizer;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.FilterBean;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.FilterFieldType;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Abstract data fetcher for base report.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseReportDataFetcher<E, B> {

    private static final String VALUES_SEPARATOR = ", ";

    @Getter
    private final String reportType;
    private final String filterTemplateType;
    private final FilterTemplateService filterTemplateService;

    private final List<FilterValueReportCustomizer> filterValueReportCustomizers = new ArrayList<>();

    /**
     * Fetches entities page for base report from database
     *
     * @param pageRequestDto - page report dto
     * @return entities page
     */
    protected abstract Page<E> getItemsPage(PageRequestDto pageRequestDto);

    /**
     * Converts entities page to report beans list
     *
     * @param page - entities page
     * @return beans list
     */
    protected abstract List<B> convertToBeans(Page<E> page);

    /**
     * Fetches report data.
     *
     * @param pageRequestDto - page request dto
     * @return base report bean
     */
    public BaseReportBean<B> fetchReportData(PageRequestDto pageRequestDto) {
        Page<E> page = getItemsPage(pageRequestDto);
        List<B> beans = convertToBeans(page);
        List<FilterBean> filterBeans = getFilterBeans(pageRequestDto);
        int totalPages = page.getTotalPages() > 0 ? page.getTotalPages() : 1;
        return BaseReportBean.<B>builder()
                .page(page.getNumber() + 1)
                .totalPages(totalPages)
                .searchQuery(pageRequestDto.getSearchQuery())
                .filters(filterBeans)
                .items(beans).build();
    }

    /**
     * Adds filter value report customizer.
     *
     * @param filterValueReportCustomizer - filter value report customizer
     */
    public void addFilterValueReportCustomizer(FilterValueReportCustomizer filterValueReportCustomizer) {
        this.filterValueReportCustomizers.add(filterValueReportCustomizer);
    }

    private List<FilterBean> getFilterBeans(PageRequestDto pageRequestDto) {
        Map<String, FilterFieldDto> filterFieldsMap = filterTemplateService.getFilterFields(filterTemplateType)
                .stream()
                .collect(Collectors.toMap(FilterFieldDto::getFieldName, Function.identity()));
        List<FilterBean> filterBeans = newArrayList();
        if (!CollectionUtils.isEmpty(pageRequestDto.getFilters())) {
            pageRequestDto.getFilters().forEach(filterRequestDto -> {
                if (!CollectionUtils.isEmpty(filterRequestDto.getValues())) {
                    List<String> values = filterRequestDto.getValues()
                            .stream()
                            .filter(StringUtils::isNotBlank)
                            .map(String::trim)
                            .collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(values)) {
                        FilterFieldDto filterFieldDto = filterFieldsMap.get(filterRequestDto.getName());
                        if (filterFieldDto != null) {
                            FilterBean filterBean = new FilterBean();
                            filterBean.setDescription(filterFieldDto.getDescription());
                            filterBean.setMatchMode(filterRequestDto.getMatchMode().name());
                            filterBean.setFilterFieldType(filterFieldDto.getFilterFieldType().name());
                            setFilterValues(filterBean, filterRequestDto, values, filterFieldDto);
                            filterBeans.add(filterBean);
                        }
                    }
                }
            });
        }
        return filterBeans;
    }

    private void setFilterValues(FilterBean filterBean,
                                 FilterRequestDto filterRequestDto,
                                 List<String> values,
                                 FilterFieldDto filterFieldDto) {
        var filterValueReportCustomizer = getFilterValueReportCustomizer(filterRequestDto);
        if (filterValueReportCustomizer != null) {
            var value = filterValueReportCustomizer.getFilterValuesAsString(values);
            filterBean.setValue1(value);
        } else {
            if (FilterFieldType.DATE.equals(filterFieldDto.getFilterFieldType())
                    && MatchMode.RANGE.equals(filterRequestDto.getMatchMode())) {
                String from = values.get(0);
                String to = values.size() > 1 ? values.get(1) : null;
                filterBean.setValue1(from);
                filterBean.setValue2(to);
            } else if (FilterFieldType.REFERENCE.equals(filterFieldDto.getFilterFieldType())
                    && Optional.ofNullable(filterFieldDto.getDictionary())
                    .map(FilterDictionaryDto::getValues).isPresent()) {
                var value = getValuesFromDictionary(values, filterFieldDto.getDictionary());
                filterBean.setValue1(value);
            } else {
                var value = StringUtils.join(values, VALUES_SEPARATOR);
                filterBean.setValue1(value);
            }
        }
    }

    private FilterValueReportCustomizer getFilterValueReportCustomizer(FilterRequestDto filterRequestDto) {
        return filterValueReportCustomizers.stream()
                .filter(filterValueReportCustomizer -> filterValueReportCustomizer.getFilterField().equals(filterRequestDto.getName()))
                .findFirst()
                .orElse(null);
    }

    protected String getDictionaryLabelByCode(String dictionaryName, String code) {
        return filterTemplateService.getFilterDictionary(dictionaryName)
                .getValues()
                .stream()
                .filter(filterDictionaryValueDto -> filterDictionaryValueDto.getValue().equals(code))
                .map(FilterDictionaryValueDto::getLabel)
                .findFirst()
                .orElse(null);
    }

    private String getValuesFromDictionary(List<String> values, FilterDictionaryDto filterDictionaryDto) {
        List<String> resultValues = filterDictionaryDto.getValues()
                .stream()
                .filter(v -> values.contains(v.getValue()))
                .map(FilterDictionaryValueDto::getLabel)
                .collect(Collectors.toList());
        return StringUtils.join(resultValues, VALUES_SEPARATOR);
    }
}
