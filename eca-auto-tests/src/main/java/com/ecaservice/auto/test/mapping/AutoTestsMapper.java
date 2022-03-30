package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.AutoTestsJobDto;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.TestFeatureEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

import java.util.stream.Collectors;

/**
 * Auto tests mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface AutoTestsMapper {

    /**
     * Maps auto tests job entity to dto model.
     *
     * @param autoTestsJobEntity - auto tests job entity
     * @return auto tests job dto
     */
    @Mapping(target = "features", ignore = true)
    AutoTestsJobDto map(AutoTestsJobEntity autoTestsJobEntity);

    /**
     * Maps features.
     *
     * @param autoTestsJobEntity - auto tests job entity
     * @param autoTestsJobDto    - auto tests job dto
     */
    @AfterMapping
    default void mapFeatures(AutoTestsJobEntity autoTestsJobEntity, @MappingTarget AutoTestsJobDto autoTestsJobDto) {
        if (!CollectionUtils.isEmpty(autoTestsJobEntity.getFeatures())) {
            var features = autoTestsJobEntity.getFeatures()
                    .stream()
                    .map(TestFeatureEntity::getTestFeature)
                    .collect(Collectors.toList());
            autoTestsJobDto.setFeatures(features);
        }
    }
}
