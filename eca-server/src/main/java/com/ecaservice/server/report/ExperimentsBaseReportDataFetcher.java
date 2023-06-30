package com.ecaservice.server.report;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.report.model.BaseReportType;
import com.ecaservice.server.report.model.ExperimentBean;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 * Experiments data fetcher for base report.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentsBaseReportDataFetcher
        extends AbstractEvaluationBaseReportDataFetcher<Experiment, ExperimentBean> {

    private final ExperimentDataService experimentDataService;
    private final ExperimentMapper experimentMapper;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterService           - filter service bean
     * @param instancesInfoRepository - instances info repository
     * @param experimentDataService   - experiments data service bean
     * @param experimentMapper        - experiment mapper bean
     */
    @Inject
    public ExperimentsBaseReportDataFetcher(FilterService filterService,
                                            InstancesInfoRepository instancesInfoRepository,
                                            ExperimentDataService experimentDataService,
                                            ExperimentMapper experimentMapper) {
        super(BaseReportType.EXPERIMENTS.name(), Experiment.class, FilterTemplateType.EXPERIMENT.name(),
                filterService, instancesInfoRepository);
        this.experimentDataService = experimentDataService;
        this.experimentMapper = experimentMapper;
    }

    @Override
    protected Page<Experiment> getItemsPage(PageRequestDto pageRequestDto) {
        return experimentDataService.getNextPage(pageRequestDto);
    }

    @Override
    protected List<ExperimentBean> convertToBeans(Page<Experiment> page) {
        return experimentMapper.mapToBeans(page.getContent());
    }
}
