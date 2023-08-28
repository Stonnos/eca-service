package com.ecaservice.server.report;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.report.data.fetcher.AbstractBaseReportDataFetcher;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.FilterTemplateType;
import com.ecaservice.server.report.customize.InstancesInfoFilterReportCustomizer;
import com.ecaservice.server.report.model.BaseReportType;
import com.ecaservice.server.report.model.ClassifierOptionsRequestBean;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.classifiers.ClassifiersTemplateProvider;
import com.ecaservice.server.service.ers.ClassifierOptionsRequestService;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;

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
    private final ClassifiersTemplateProvider classifiersTemplateProvider;
    private final InstancesInfoRepository instancesInfoRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterTemplateService                       - filter service bean
     * @param instancesInfoRepository             - instances info repository
     * @param classifierOptionsRequestService     - classifier options request service bean
     * @param classifierOptionsRequestModelMapper - classifier options request model mapper bean
     * @param classifiersTemplateProvider         - classifiers template provider
     */
    @Inject
    public ClassifierOptionsRequestsBaseReportDataFetcher(FilterTemplateService filterTemplateService,
                                                          InstancesInfoRepository instancesInfoRepository,
                                                          ClassifierOptionsRequestService classifierOptionsRequestService,
                                                          ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper,
                                                          ClassifiersTemplateProvider classifiersTemplateProvider) {
        super(BaseReportType.CLASSIFIERS_OPTIONS_REQUESTS.name(),
                FilterTemplateType.CLASSIFIER_OPTIONS_REQUEST, filterTemplateService);
        this.classifierOptionsRequestService = classifierOptionsRequestService;
        this.classifierOptionsRequestModelMapper = classifierOptionsRequestModelMapper;
        this.classifiersTemplateProvider = classifiersTemplateProvider;
        this.instancesInfoRepository = instancesInfoRepository;
    }

    @PostConstruct
    public void init() {
        addFilterValueReportCustomizer(new InstancesInfoFilterReportCustomizer(instancesInfoRepository));
    }

    @Override
    protected Page<ClassifierOptionsRequestModel> getItemsPage(PageRequestDto pageRequestDto) {
        return classifierOptionsRequestService.getNextPage(pageRequestDto);
    }

    @Override
    protected List<ClassifierOptionsRequestBean> convertToBeans(Page<ClassifierOptionsRequestModel> page) {
        return page.getContent()
                .stream()
                .map(classifierOptionsRequestModel -> {
                    var classifierOptionsRequestBean =
                            classifierOptionsRequestModelMapper.mapToBean(classifierOptionsRequestModel);
                    if (!CollectionUtils.isEmpty(classifierOptionsRequestModel.getClassifierOptionsResponseModels())) {
                        var classifierOptionsResponseModel =
                                classifierOptionsRequestModel.getClassifierOptionsResponseModels().iterator().next();
                        var classifierOptions = parseOptions(classifierOptionsResponseModel.getOptions());
                        var template = classifiersTemplateProvider.getTemplate(classifierOptions);
                        classifierOptionsRequestBean.setClassifierName(template.getTemplateTitle());
                        classifierOptionsRequestBean.setClassifierOptions(classifierOptionsResponseModel.getOptions());
                    }
                    return classifierOptionsRequestBean;
                })
                .collect(Collectors.toList());
    }
}
