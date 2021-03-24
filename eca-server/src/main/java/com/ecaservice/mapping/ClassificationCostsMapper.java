package com.ecaservice.mapping;

import com.ecaservice.ers.dto.ClassificationCostsReport;
import com.ecaservice.web.dto.model.ClassificationCostsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Classification costs mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ClassificationCostsMapper {

    /**
     * Maps classification costs report to classification costs dto model.
     *
     * @param classificationCostsReport - classification costs report
     * @return classification costs dto
     */
    @Mapping(source = "rocCurve.aucValue", target = "aucValue")
    ClassificationCostsDto map(ClassificationCostsReport classificationCostsReport);

    /**
     * Maps classification costs reports list to classification costs dto list.
     *
     * @param classificationCostsReports - classification costs reports list
     * @return classification costs dto list
     */
    List<ClassificationCostsDto> map(List<ClassificationCostsReport> classificationCostsReports);
}
