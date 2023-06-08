package com.ecaservice.data.storage.mapping;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.model.report.ReportProperties;
import com.ecaservice.web.dto.model.InstancesDto;
import com.ecaservice.web.dto.model.InstancesReportInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Instances mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface InstancesMapper {

    /**
     * Maps instances entity to dto model.
     *
     * @param instancesEntity - instances entity
     * @return instances dto
     */
    @Mapping(source = "classAttribute.id", target = "classAttributeId")
    InstancesDto map(InstancesEntity instancesEntity);

    /**
     * Maps instances entities list to dto models list.
     *
     * @param instancesEntityList - instances entities list
     * @return instances dto list
     */
    List<InstancesDto> map(List<InstancesEntity> instancesEntityList);

    /**
     * Maps report properties to report info dto.
     *
     * @param reportProperties - report properties
     * @return report info dto
     */
    @Mapping(source = "reportType.extension", target = "fileExtension")
    InstancesReportInfoDto mapReportProperties(ReportProperties reportProperties);

    /**
     * Maps report properties list to reports info dto list .
     *
     * @param reportPropertiesList - report properties list
     * @return report info dto list
     */
    List<InstancesReportInfoDto> mapReportPropertiesList(List<ReportProperties> reportPropertiesList);
}
