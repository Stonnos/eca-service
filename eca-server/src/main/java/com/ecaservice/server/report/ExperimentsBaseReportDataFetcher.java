package com.ecaservice.server.report;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.report.data.fetcher.AbstractBaseReportDataFetcher;
import com.ecaservice.server.config.AppProperties;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.report.customize.InstancesInfoFilterReportCustomizer;
import com.ecaservice.server.report.model.BaseReportType;
import com.ecaservice.server.report.model.ExperimentBean;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import com.ecaservice.web.dto.model.PageRequestDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.server.util.RoutePaths.EXPERIMENT_DETAILS_PATH;

/**
 * Experiments data fetcher for base report.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentsBaseReportDataFetcher
        extends AbstractBaseReportDataFetcher<Experiment, ExperimentBean> {

    private final AppProperties appProperties;
    private final ExperimentDataService experimentDataService;
    private final ExperimentMapper experimentMapper;
    private final InstancesInfoRepository instancesInfoRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterTemplateService   - filter service bean
     * @param appProperties           - app properties
     * @param instancesInfoRepository - instances info repository
     * @param experimentDataService   - experiments data service bean
     * @param experimentMapper        - experiment mapper bean
     */
    @Autowired
    public ExperimentsBaseReportDataFetcher(FilterTemplateService filterTemplateService,
                                            AppProperties appProperties,
                                            InstancesInfoRepository instancesInfoRepository,
                                            ExperimentDataService experimentDataService,
                                            ExperimentMapper experimentMapper) {
        super(BaseReportType.EXPERIMENTS.name(), FilterTemplateType.EXPERIMENT, appProperties.getMaxPagesNum(),
                filterTemplateService);
        this.appProperties = appProperties;
        this.experimentDataService = experimentDataService;
        this.experimentMapper = experimentMapper;
        this.instancesInfoRepository = instancesInfoRepository;
    }

    @PostConstruct
    public void init() {
        addFilterValueReportCustomizer(new InstancesInfoFilterReportCustomizer(instancesInfoRepository));
    }

    @Override
    protected Page<Experiment> getItemsPage(PageRequestDto pageRequestDto) {
        return experimentDataService.getNextPage(pageRequestDto);
    }

    @Override
    protected List<ExperimentBean> convertToBeans(Page<Experiment> page) {
        return page.getContent()
                .stream()
                .map(experiment -> {
                    String externalDetailsUrl = getExternalDetailsUrl(experiment);
                    ExperimentBean experimentBean = experimentMapper.mapToBean(experiment);
                    experimentBean.setExternalDetailsUrl(externalDetailsUrl);
                    return experimentBean;
                })
                .collect(Collectors.toList());
    }

    private String getExternalDetailsUrl(Experiment experiment) {
        String path = String.format(EXPERIMENT_DETAILS_PATH, experiment.getId());
        return String.format("%s%s", appProperties.getWebExternalBaseUrl(), path);
    }
}
