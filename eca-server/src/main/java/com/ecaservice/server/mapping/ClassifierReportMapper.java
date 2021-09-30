package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.server.model.entity.ClassifierOptionsResponseModel;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Implements mapping classifier report to classifier options response model.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ClassifierReportMapper {

    /**
     * Maps classifier report to classifier options response model.
     *
     * @param classifierReport - classifier report
     * @return classifier options response model
     */
    ClassifierOptionsResponseModel map(ClassifierReport classifierReport);

    /**
     * Maps classifier report list to classifier options response model list.
     *
     * @param classifierReports classifier report list
     * @return classifier options response model list
     */
    List<ClassifierOptionsResponseModel> map(List<ClassifierReport> classifierReports);
}
