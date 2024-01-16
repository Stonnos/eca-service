package com.ecaservice.ers.report.customize;

import com.ecaservice.ers.repository.InstancesInfoRepository;
import com.ecaservice.report.data.customize.FilterValueReportCustomizer;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Instances info filter value customizer.
 *
 * @author Roman Batygin
 */
public class InstancesInfoFilterReportCustomizer extends FilterValueReportCustomizer {

    private static final String VALUES_SEPARATOR = ", ";

    private final InstancesInfoRepository instancesInfoRepository;

    /**
     * Constructor with parameters.
     *
     * @param instancesInfoRepository - instances info repository
     */
    public InstancesInfoFilterReportCustomizer(InstancesInfoRepository instancesInfoRepository) {
        super("instancesInfo.id");
        this.instancesInfoRepository = instancesInfoRepository;
    }

    @Override
    public String getFilterValuesAsString(List<String> values) {
        List<Long> ids = values.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());
        var instancesInfoNamesList = instancesInfoRepository.getInstancesNames(ids);
        return StringUtils.join(instancesInfoNamesList, VALUES_SEPARATOR);
    }
}
