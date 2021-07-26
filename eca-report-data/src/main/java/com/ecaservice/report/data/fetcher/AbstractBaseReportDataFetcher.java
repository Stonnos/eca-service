package com.ecaservice.report.data.fetcher;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.FilterBean;
import com.ecaservice.report.model.ReportType;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.ReflectionUtils.getFieldType;
import static com.ecaservice.report.data.util.RangeUtils.formatDateRange;
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
    private final ReportType reportType;
    private final Class<E> entityClazz;
    private final String filterTemplateType;
    private final FilterService filterService;

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

    private List<FilterBean> getFilterBeans(PageRequestDto pageRequestDto) {
        Map<String, FilterFieldDto> filterFieldsMap = filterService.getFilterFields(filterTemplateType)
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
                        FilterBean filterBean = new FilterBean();
                        FilterFieldDto filterFieldDto = filterFieldsMap.get(filterRequestDto.getName());
                        String description =
                                Optional.ofNullable(filterFieldDto).map(FilterFieldDto::getDescription).orElse(null);
                        filterBean.setDescription(description);
                        String filterData = getFilterValuesAsString(filterRequestDto, values, filterFieldDto);
                        filterBean.setFilterData(filterData);
                        filterBeans.add(filterBean);
                    }
                }
            });
        }
        return filterBeans;
    }

    private String getFilterValuesAsString(FilterRequestDto filterRequestDto, List<String> values,
                                           FilterFieldDto filterFieldDto) {
        if (MatchMode.RANGE.equals(filterRequestDto.getMatchMode())) {
            return getRangeAsString(values, filterRequestDto);
        } else {
            if (Optional.ofNullable(filterFieldDto).map(FilterFieldDto::getDictionary).map(
                    FilterDictionaryDto::getValues).isPresent()) {
                return getValuesFromDictionary(values, filterFieldDto.getDictionary());
            }
            return StringUtils.join(values, VALUES_SEPARATOR);
        }
    }

    private String getValuesFromDictionary(List<String> values, FilterDictionaryDto filterDictionaryDto) {
        List<String> resultValues = filterDictionaryDto.getValues()
                .stream()
                .filter(v -> values.contains(v.getValue()))
                .map(FilterDictionaryValueDto::getLabel)
                .collect(Collectors.toList());
        return StringUtils.join(resultValues, VALUES_SEPARATOR);
    }

    private String getRangeAsString(List<String> values, FilterRequestDto filterRequestDto) {
        Class fieldClazz = getFieldType(filterRequestDto.getName(), entityClazz);
        if (!LocalDateTime.class.isAssignableFrom(fieldClazz)) {
            throw new IllegalStateException(
                    String.format("Can't get range value as string for field class %s", fieldClazz.getSimpleName()));
        } else {
            String lowerBound = values.get(0);
            String upperBound = values.size() > 1 ? values.get(1) : null;
            return formatDateRange(lowerBound, upperBound);
        }
    }
}
