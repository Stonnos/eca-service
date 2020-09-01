package com.ecaservice.report;

import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.report.model.ExperimentBean;
import com.ecaservice.report.model.ReportType;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.service.filter.FilterService;
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
public class ExperimentsBaseReportDataFetcher extends AbstractBaseReportDataFetcher<Experiment, ExperimentBean> {

    private final ExperimentService experimentService;
    private final ExperimentMapper experimentMapper;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterService     - filter service bean
     * @param experimentService - experiments service bean
     * @param experimentMapper  - experiment mapper bean
     */
    @Inject
    public ExperimentsBaseReportDataFetcher(FilterService filterService,
                                            ExperimentService experimentService,
                                            ExperimentMapper experimentMapper) {
        super(ReportType.EXPERIMENTS, Experiment.class, FilterTemplateType.EXPERIMENT, filterService);
        this.experimentService = experimentService;
        this.experimentMapper = experimentMapper;
    }

    @Override
    protected Page<Experiment> getItemsPage(PageRequestDto pageRequestDto) {
        return experimentService.getNextPage(pageRequestDto);
    }

    @Override
    protected List<ExperimentBean> convertToBeans(Page<Experiment> page) {
        return experimentMapper.mapToBeans(page.getContent());
    }
}
