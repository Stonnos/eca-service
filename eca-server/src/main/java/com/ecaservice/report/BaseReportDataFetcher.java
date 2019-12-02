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
        List<FilterBean> filterBeans = getFilterBeans(pageRequestDto, FilterTemplateType.EXPERIMENT);
        return new BaseReportBean<>(experimentPage.getNumber(), experimentPage.getTotalPages(),
                pageRequestDto.getSearchQuery(), filterBeans, experimentBeans);
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
        List<FilterBean> filterBeans = getFilterBeans(pageRequestDto, FilterTemplateType.EVALUATION_LOG);
        return new BaseReportBean<>(evaluationLogPage.getNumber(), evaluationLogPage.getTotalPages(),
                pageRequestDto.getSearchQuery(), filterBeans, evaluationLogBeans);
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
