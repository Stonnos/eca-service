package com.ecaservice.report.data.fetcher;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.FilterBean;
import com.ecaservice.report.model.ReportType;
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
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    private static final String GET_ENUM_DESCRIPTION_METHOD_NAME = "getDescription";

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
        Map<String, String> filterFieldsMap = filterService.getFilterFields(filterTemplateType)
                .stream()
                .collect(Collectors.toMap(FilterFieldDto::getFieldName, FilterFieldDto::getDescription));
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
        List<String> enumValues;
        Method method = ReflectionUtils.findMethod(fieldClazz, GET_ENUM_DESCRIPTION_METHOD_NAME);
        if (method != null) {
            enumValues = values.stream()
                    .map(value -> {
                        Enum<?> enumVal = Enum.valueOf(fieldClazz, value);
                        Object retVal = ReflectionUtils.invokeMethod(method, enumVal);
                        return Optional.ofNullable(retVal).map(String::valueOf).orElse(null);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            enumValues = values;
        }
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
