package com.ecaservice.report;

import com.ecaservice.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.report.model.ClassifierOptionsRequestBean;
import com.ecaservice.report.model.ReportType;
import com.ecaservice.service.ers.ClassifierOptionsRequestService;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 * Classifier options requests data fetcher for base report.
 *
 * @author Roman Batygin
 */
@Component
public class ClassifierOptionsRequestsBaseReportDataFetcher
        extends AbstractBaseReportDataFetcher<ClassifierOptionsRequestModel, ClassifierOptionsRequestBean> {

    private final ClassifierOptionsRequestService classifierOptionsRequestService;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterService                       - filter service bean
     * @param classifierOptionsRequestService     - classifier options request service bean
     * @param classifierOptionsRequestModelMapper - classifier options request model mapper bean
     */
    @Inject
    public ClassifierOptionsRequestsBaseReportDataFetcher(FilterService filterService,
                                                          ClassifierOptionsRequestService classifierOptionsRequestService,
                                                          ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper) {
        super(ReportType.CLASSIFIERS_OPTIONS_REQUESTS, ClassifierOptionsRequestModel.class,
                FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST, filterService);
        this.classifierOptionsRequestService = classifierOptionsRequestService;
        this.classifierOptionsRequestModelMapper = classifierOptionsRequestModelMapper;
    }

    @Override
    protected Page<ClassifierOptionsRequestModel> getItemsPage(PageRequestDto pageRequestDto) {
        return classifierOptionsRequestService.getNextPage(pageRequestDto);
    }

    @Override
    protected List<ClassifierOptionsRequestBean> convertToBeans(Page<ClassifierOptionsRequestModel> page) {
        return classifierOptionsRequestModelMapper.mapToBeans(page.getContent());
    }
}
