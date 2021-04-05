package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.InstancesReport;
import com.ecaservice.ers.model.InstancesInfo;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Instances report mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface InstancesMapper {

    /**
     * Maps instances report to instances info entity.
     *
     * @param instancesReport - instances report entity
     * @return instances info entity
     */
    @Mapping(target = "structure", ignore = true)
    InstancesInfo map(InstancesReport instancesReport);

    /**
     * Maps instances info entity to dto model.
     *
     * @param instancesInfo - instances info entity
     * @return instances report
     */
    @Mapping(target = "structure", ignore = true)
    InstancesReport map(InstancesInfo instancesInfo);

    @AfterMapping
    default void mapStructure(InstancesInfo instancesInfo, @MappingTarget InstancesReport instancesReport) {
        instancesReport.setStructure(Optional.ofNullable(instancesInfo.getStructure()).map(
                data -> new String(data, StandardCharsets.UTF_8)).orElse(null));
    }
}
