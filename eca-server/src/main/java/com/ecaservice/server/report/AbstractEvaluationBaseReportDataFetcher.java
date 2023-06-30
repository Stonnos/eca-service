package com.ecaservice.server.report;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.report.data.fetcher.AbstractBaseReportDataFetcher;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.web.dto.model.FilterFieldDto;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract evaluation data fetcher for base report.
 *
 * @author Roman Batygin
 */
public abstract class AbstractEvaluationBaseReportDataFetcher<E, B> extends AbstractBaseReportDataFetcher<E, B> {

    private static final String INSTANCES_INFO_ID_FILTER_FIELD = "instancesInfo.id";

    private final InstancesInfoRepository instancesInfoRepository;

    protected AbstractEvaluationBaseReportDataFetcher(String reportType,
                                                      Class<E> entityClazz,
                                                      String filterTemplateType,
                                                      FilterService filterService,
                                                      InstancesInfoRepository instancesInfoRepository) {
        super(reportType, entityClazz, filterTemplateType, filterService);
        this.instancesInfoRepository = instancesInfoRepository;
    }

    @Override
    protected String getLazyReferenceFilterValuesAsString(List<String> values,
                                                          FilterFieldDto filterFieldDto) {
        if (INSTANCES_INFO_ID_FILTER_FIELD.equals(filterFieldDto.getFieldName())) {
            List<Long> ids = values.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            var instancesInfoNamesList = instancesInfoRepository.getInstancesNames(ids);
            return StringUtils.join(instancesInfoNamesList, VALUES_SEPARATOR);
        } else {
            return super.getLazyReferenceFilterValuesAsString(values, filterFieldDto);
        }
    }
}
