package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.ClassifierInputOption;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.dto.EnsembleClassifierReport;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements mapping classifier options info to dto model.
 *
 * @author Roman Batygin
 */
@Mapper(uses = ClassifierReportFactory.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ClassifierOptionsInfoMapper {

    /**
     * Maps classifier options info to dto model.
     *
     * @param classifierOptionsInfo - classifier options info
     * @return classifier options report
     */
    public abstract ClassifierReport map(ClassifierOptionsInfo classifierOptionsInfo);

    /**
     * Maps classifier options info list to dto models list.
     *
     * @param classifierOptionsInfoList - classifier options info list
     * @return classifier options dto list
     */
    public abstract List<ClassifierReport> map(List<ClassifierOptionsInfo> classifierOptionsInfoList);

    @AfterMapping
    protected void mapInputOptions(ClassifierOptionsInfo classifierOptionsInfo,
                                   @MappingTarget ClassifierReport classifierReport) {
        if (!CollectionUtils.isEmpty(classifierOptionsInfo.getInputOptionsMap())) {
            List<ClassifierInputOption> classifierInputOptions = classifierOptionsInfo.getInputOptionsMap()
                    .entrySet().stream()
                    .map(entry -> new ClassifierInputOption(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
            classifierReport.setClassifierInputOptions(classifierInputOptions);
        }
    }

    @AfterMapping
    protected void mapClassifiers(ClassifierOptionsInfo classifierOptionsInfo,
                                  @MappingTarget ClassifierReport classifierReport) {
        if (classifierReport instanceof EnsembleClassifierReport) {
            EnsembleClassifierReport ensembleClassifierReport = (EnsembleClassifierReport) classifierReport;
            List<ClassifierReport> classifierReports = map(classifierOptionsInfo.getIndividualClassifiers());
            ensembleClassifierReport.setIndividualClassifiers(classifierReports);
        }
    }
}
