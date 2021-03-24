package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.ClassificationCostsReport;
import com.ecaservice.ers.model.ClassificationCostsInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

/**
 * Classification costs report mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = RocCurveReportMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassificationCostsReportMapper {

    /**
     * Maps classification costs report to classification costs info entity.
     *
     * @param classificationCostsReport -  classification costs report
     * @return classification costs info entity
     */
    @Mapping(source = "classificationCostsReport.rocCurve", target = "rocCurveInfo")
    ClassificationCostsInfo map(ClassificationCostsReport classificationCostsReport);

    /**
     * Maps classification costs reports to classification costs info list.
     *
     * @param classificationCostsReports -  classification costs reports list
     * @return classification costs info list
     */
    Set<ClassificationCostsInfo> map(List<ClassificationCostsReport> classificationCostsReports);

    /**
     * Maps classification costs entity to its dto model.
     *
     * @param classificationCostsInfo - classification costs entity
     * @return classification costs dto
     */
    @Mapping(source = "rocCurveInfo", target = "rocCurve")
    ClassificationCostsReport map(ClassificationCostsInfo classificationCostsInfo);
}
