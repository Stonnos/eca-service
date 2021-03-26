package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.dto.EnsembleClassifierReport;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import org.mapstruct.ObjectFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * Classifier report factory.
 *
 * @author Roman Batygin
 */
@Component
public class ClassifierReportFactory {

    /**
     * Creates classifier report.
     *
     * @param classifierOptionsInfo - classifier options info
     * @return classifier report
     */
    @ObjectFactory
    public ClassifierReport createClassifierReport(ClassifierOptionsInfo classifierOptionsInfo) {
        return CollectionUtils.isEmpty(classifierOptionsInfo.getIndividualClassifiers()) ? new ClassifierReport() :
                new EnsembleClassifierReport();
    }
}
