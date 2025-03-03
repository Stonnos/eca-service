package com.ecaservice.server.mapping;

import com.ecaservice.data.loader.dto.AttributeInfo;
import com.ecaservice.data.loader.dto.InstancesMetaInfoDto;
import com.ecaservice.ers.dto.InstancesReport;
import com.ecaservice.server.model.data.AttributeMetaInfo;
import com.ecaservice.server.model.data.AttributeType;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.web.dto.model.AttributeMetaInfoDto;
import com.ecaservice.web.dto.model.AttributeValueMetaInfoDto;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Implements mapping instances info entity to its dto model.
 */
@Mapper
public interface InstancesInfoMapper {

    /**
     * Maps instances info to its dto model.
     *
     * @param instancesInfo - instances info entity
     * @return instances info dto
     */
    InstancesInfoDto map(InstancesInfo instancesInfo);

    /**
     * Maps instances info list to dto models list.
     *
     * @param instancesInfoList - instances info entities list
     * @return instances info dto list
     */
    List<InstancesInfoDto> map(List<InstancesInfo> instancesInfoList);

    /**
     * Maps instances info to instances report.
     *
     * @param instancesInfo - instances info
     * @return instances report
     */
    InstancesReport mapToReport(InstancesInfo instancesInfo);

    /**
     * Maps instances meta info dto to internal meta data model.
     *
     * @param instancesMetaInfoDto - instances meta info dto
     * @return instances  meta data model
     */
    InstancesMetaDataModel map(InstancesMetaInfoDto instancesMetaInfoDto);

    /**
     * Maps attribute info dto to internal attribute meta info.
     *
     * @param attributeInfo - attribute info dto
     * @return attribute info
     */
    AttributeMetaInfo map(AttributeInfo attributeInfo);

    /**
     * Maps attribute meta info to dto model.
     *
     * @param attributeMetaInfo - attribute meta info
     * @return attribute meta info dto
     */
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "values", ignore = true)
    AttributeMetaInfoDto map(AttributeMetaInfo attributeMetaInfo);

    /**
     * Maps attribute values.
     *
     * @param attributeMetaInfo    - attribute meta info
     * @param attributeMetaInfoDto - attribute meta info dto
     */
    @AfterMapping
    default void mapAttributeValues(AttributeMetaInfo attributeMetaInfo,
                                    @MappingTarget AttributeMetaInfoDto attributeMetaInfoDto) {
        if (AttributeType.NOMINAL.equals(attributeMetaInfo.getType())) {
            List<AttributeValueMetaInfoDto> attributeValueMetaInfoList =
                    IntStream.range(0, attributeMetaInfo.getValues().size()).mapToObj(i -> {
                        AttributeValueMetaInfoDto attributeValueMetaInfoDto = new AttributeValueMetaInfoDto();
                        attributeValueMetaInfoDto.setIndex(i);
                        attributeValueMetaInfoDto.setValue(attributeMetaInfo.getValues().get(i));
                        return attributeValueMetaInfoDto;
                    }).toList();
            attributeMetaInfoDto.setValues(attributeValueMetaInfoList);
        }
    }

    /**
     * Maps attribute type.
     *
     * @param attributeMetaInfo    - attribute meta info
     * @param attributeMetaInfoDto - attribute meta info dto
     */
    @AfterMapping
    default void mapAttributeType(AttributeMetaInfo attributeMetaInfo,
                                  @MappingTarget AttributeMetaInfoDto attributeMetaInfoDto) {
        attributeMetaInfoDto.setType(
                new EnumDto(attributeMetaInfo.getType().name(), attributeMetaInfo.getType().getDescription()));
    }
}
