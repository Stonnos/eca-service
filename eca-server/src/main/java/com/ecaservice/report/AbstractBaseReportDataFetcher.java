package com.ecaservice.report;

import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.FilterBean;
import com.ecaservice.report.model.ReportType;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.core.DescriptiveEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.ReflectionUtils.getFieldType;
import static com.ecaservice.util.RangeUtils.formatDateRange;
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
    private final FilterTemplateType filterTemplateType;
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
        Map<String, String> filterFieldsMap = filterService.getFilterFields(filterTemplateType.name()).stream().collect(
                Collectors.toMap(FilterFieldDto::getFieldName, FilterFieldDto::getDescription));
        List<FilterBean> filterBeans = newArrayList();
        if (!CollectionUtils.isEmpty(pageRequestDto.getFilters())) {
            pageRequestDto.getFilters().forEach(filterRequestDto -> {
                if (!CollectionUtils.isEmpty(filterRequestDto.getValues())) {
                    List<String> values = filterRequestDto.getValues().stream().filter(StringUtils::isNotBlank).map(
                            String::trim).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(values)) {
                        FilterBean filterBean = new FilterBean();
                        filterBean.setDescription(filterFieldsMap.get(filterRequestDto.getName()));
                        String filterData = getFilterValuesAsString(filterRequestDto, values);
                        filterBean.setFilterData(filterData);
                        filterBeans.add(filterBean);
                    }
                }
            });
        }
        return filterBeans;
    }

    private String getFilterValuesAsString(FilterRequestDto filterRequestDto, List<String> values) {
        Class fieldClazz = getFieldType(filterRequestDto.getName(), entityClazz);
        if (MatchMode.RANGE.equals(filterRequestDto.getMatchMode())) {
            return getRangeAsString(values, fieldClazz);
        } else {
            if (fieldClazz.isEnum()) {
                return getEnumValuesAsString(values, fieldClazz);
            }
            return StringUtils.join(values, VALUES_SEPARATOR);
        }
    }

    private String getEnumValuesAsString(List<String> values, Class fieldClazz) {
        if (!DescriptiveEnum.class.isAssignableFrom(fieldClazz)) {
            throw new IllegalStateException(
                    String.format("Enum class [%s] must implements [%s] interface!", fieldClazz.getSimpleName(),
                            DescriptiveEnum.class.getSimpleName()));
        }
        List<String> enumValues = values.stream()
                .map(value -> ((DescriptiveEnum) Enum.valueOf(fieldClazz, value)).getDescription())
                .collect(Collectors.toList());
        return StringUtils.join(enumValues, VALUES_SEPARATOR);
    }

    private String getRangeAsString(List<String> values, Class fieldClazz) {
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
