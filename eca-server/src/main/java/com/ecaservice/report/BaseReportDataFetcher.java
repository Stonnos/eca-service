package com.ecaservice.report;

import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.EvaluationLogBean;
import com.ecaservice.report.model.ExperimentBean;
import com.ecaservice.report.model.FilterBean;
import com.ecaservice.service.evaluation.EvaluationLogService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.filter.FilterService;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.core.DescriptiveEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ecaservice.util.RangeUtils.formatDateRange;
import static com.ecaservice.util.ReflectionUtils.getGetterReturnType;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Data fetcher for base report.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class BaseReportDataFetcher {

    private static final String VALUES_SEPARATOR = ", ";

    private final FilterService filterService;
    private final ExperimentService experimentService;
    private final EvaluationLogService evaluationLogService;
    private final ExperimentMapper experimentMapper;
    private final EvaluationLogMapper evaluationLogMapper;

    /**
     * Gets experiments report data.
     *
     * @param pageRequestDto - page request dto
     * @return base report bean
     */
    public BaseReportBean<ExperimentBean> fetchExperimentsData(PageRequestDto pageRequestDto) {
        Page<Experiment> experimentPage = experimentService.getNextPage(pageRequestDto);
        List<ExperimentBean> experimentBeans = experimentMapper.mapToBeans(experimentPage.getContent());
        List<FilterBean> filterBeans = getFilterBeans(pageRequestDto, FilterTemplateType.EXPERIMENT, Experiment.class);
        return createBaseReportBean(pageRequestDto, experimentPage, experimentBeans, filterBeans);
    }

    /**
     * Gets evaluation logs report data.
     *
     * @param pageRequestDto - page request dto
     * @return base report bean
     */
    public BaseReportBean<EvaluationLogBean> fetchEvaluationLogs(PageRequestDto pageRequestDto) {
        Page<EvaluationLog> evaluationLogPage = evaluationLogService.getNextPage(pageRequestDto);
        List<EvaluationLogBean> evaluationLogBeans = evaluationLogMapper.mapToBeans(evaluationLogPage.getContent());
        List<FilterBean> filterBeans =
                getFilterBeans(pageRequestDto, FilterTemplateType.EVALUATION_LOG, EvaluationLog.class);
        return createBaseReportBean(pageRequestDto, evaluationLogPage, evaluationLogBeans, filterBeans);
    }

    private <E, B> BaseReportBean<B> createBaseReportBean(PageRequestDto pageRequestDto, Page<E> page, List<B> beans,
                                                          List<FilterBean> filterBeans) {
        int pageNumber = page.getTotalPages() > 0 ? page.getNumber() + 1 : page.getNumber();
        return new BaseReportBean<>(pageNumber, page.getTotalPages(), pageRequestDto.getSearchQuery(), filterBeans,
                beans);
    }

    private List<FilterBean> getFilterBeans(PageRequestDto pageRequestDto, FilterTemplateType filterTemplateType,
                                            Class<?> entityClazz) {
        Map<String, String> filterFieldsMap = filterService.getFilterFields(filterTemplateType).stream().collect(
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
                        String filterData = getFilterValuesAsString(filterRequestDto, values, entityClazz);
                        filterBean.setFilterData(filterData);
                        filterBeans.add(filterBean);
                    }
                }
            });
        }
        return filterBeans;
    }

    private String getFilterValuesAsString(FilterRequestDto filterRequestDto, List<String> values,
                                           Class<?> entityClazz) {
        Class fieldClazz = getGetterReturnType(filterRequestDto.getName(), entityClazz);
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
