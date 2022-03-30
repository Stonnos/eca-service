package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.TestFeatureDto;
import com.ecaservice.auto.test.entity.autotest.TestFeatureEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Test feature mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface TestFeatureMapper {

    /**
     * Maps test feature entity to dto model.
     *
     * @param testFeatureEntity - test feature entity
     * @return test feature dto model
     */
    @Mapping(source = "testFeature", target = "feature")
    @Mapping(source = "testFeature.description", target = "featureDescription")
    TestFeatureDto map(TestFeatureEntity testFeatureEntity);

    /**
     * Maps test feature entities to dto model
     *
     * @param testFeatures - test feature entities
     * @return test feature dto models
     */
    List<TestFeatureDto> map(List<TestFeatureEntity> testFeatures);
}
