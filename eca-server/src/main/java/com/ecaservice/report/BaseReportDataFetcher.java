package com.ecaservice.report;

import com.ecaservice.mapping.report.BaseReportMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.report.model.BaseReportBean;
import com.ecaservice.report.model.ExperimentBean;
import com.ecaservice.report.model.FilterBean;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.filter.FilterService;
import com.ecaservice.web.dto.model.FilterFieldDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Data fetcher for base report.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class BaseReportDataFetcher {

    private final FilterService filterService;
    private final ExperimentService experimentService;
    private final BaseReportMapper baseReportMapper;

    /**
     * Gets experiments report data.
     *
     * @param pageRequestDto - page request dto
     * @return base report bean
     */
    public BaseReportBean<ExperimentBean> fetchExperimentsData(PageRequestDto pageRequestDto) {
        Page<Experiment> experimentPage = experimentService.getNextPage(pageRequestDto);
        BaseReportBean<ExperimentBean> baseReportBean = baseReportMapper.map(experimentPage, pageRequestDto);
        List<FilterBean> filterBeans = getFilterBeans(pageRequestDto, FilterTemplateType.EXPERIMENT);
        baseReportBean.setFilters(filterBeans);
        return baseReportBean;
    }

    private List<FilterBean> getFilterBeans(PageRequestDto pageRequestDto, FilterTemplateType filterTemplateType) {
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
                        filterBeans.add(filterBean);
                    }
                }
            });
        }
        return filterBeans;
    }
}
