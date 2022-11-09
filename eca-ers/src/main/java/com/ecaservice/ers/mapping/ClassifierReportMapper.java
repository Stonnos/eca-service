package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import org.mapstruct.Mapper;

/**
 * Implements mapping classifier report to classifier options info entity.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ClassifierReportMapper {

    /**
     * Maps classifier report to classifier options info entity.
     *
     * @param classifierReport - classifier report
     * @return classifier options info entity
     */
    ClassifierOptionsInfo map(ClassifierReport classifierReport);
}
