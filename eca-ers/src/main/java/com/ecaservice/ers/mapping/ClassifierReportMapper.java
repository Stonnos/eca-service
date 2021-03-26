package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.ClassifierInputOption;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.dto.EnsembleClassifierReport;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implements mapping classifier report to classifier options info entity.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class ClassifierReportMapper {

    /**
     * Maps classifier report to classifier options info entity.
     *
     * @param classifierReport - classifier report
     * @return classifier options info entity
     */
    @Mapping(target = "inputOptionsMap", ignore = true)
    public abstract ClassifierOptionsInfo map(ClassifierReport classifierReport);

    @AfterMapping
    protected void mapInputOptions(ClassifierReport classifierReport,
                                   @MappingTarget ClassifierOptionsInfo classifierOptionsInfo) {
        if (!CollectionUtils.isEmpty(classifierReport.getClassifierInputOptions())) {
            Map<String, String> inputOptionsMap = classifierReport.getClassifierInputOptions().stream()
                    .collect(Collectors.toMap(ClassifierInputOption::getKey, ClassifierInputOption::getValue));
            classifierOptionsInfo.setInputOptionsMap(inputOptionsMap);
        }
    }

    /**
     * Maps individual classifiers reports for ensemble algorithms.
     *
     * @param classifierReport      - classifier report
     * @param classifierOptionsInfo classifier options info entity
     */
    @AfterMapping
    protected void mapClassifiers(ClassifierReport classifierReport,
                                  @MappingTarget ClassifierOptionsInfo classifierOptionsInfo) {
        if (classifierReport instanceof EnsembleClassifierReport) {
            EnsembleClassifierReport ensembleClassifierReport = (EnsembleClassifierReport) classifierReport;
            List<ClassifierReport> classifierReports = ensembleClassifierReport.getIndividualClassifiers();
            List<ClassifierOptionsInfo> individualClassifiers = classifierReports.stream()
                    .map(this::map)
                    .collect(Collectors.toList());
            classifierOptionsInfo.setIndividualClassifiers(individualClassifiers);
        }
    }
}
