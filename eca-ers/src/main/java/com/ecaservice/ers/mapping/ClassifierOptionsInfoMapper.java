package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Implements mapping classifier options info to dto model.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ClassifierOptionsInfoMapper {

    /**
     * Maps classifier options info to dto model.
     *
     * @param classifierOptionsInfo - classifier options info
     * @return classifier options report
     */
    ClassifierReport map(ClassifierOptionsInfo classifierOptionsInfo);

    /**
     * Maps classifier options info list to dto models list.
     *
     * @param classifierOptionsInfoList - classifier options info list
     * @return classifier options dto list
     */
    List<ClassifierReport> map(List<ClassifierOptionsInfo> classifierOptionsInfoList);
}
