package com.ecaservice.data.storage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import weka.core.Attribute;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.IntStream;

import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link InstancesConversionService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({InstancesConversionService.class})
class InstancesConversionServiceTest {

    @Inject
    private InstancesConversionService instancesConversionService;

    private Instances instances;

    @BeforeEach
    void init() {
        instances = loadInstances();
    }

    @Test
    void testInstancesConversion() {
        var dataList = instancesConversionService.covert(instances);
        assertThat(dataList).isNotNull();
        assertThat(dataList).hasSize(instances.numInstances());
        assertRows(dataList);
    }

    private void assertRows(List<List<String>> dataList) {
        IntStream.range(0, instances.numInstances()).forEach(i -> {
            var expected = instances.instance(i);
            var actual = dataList.get(i);
            IntStream.range(0, instances.numAttributes()).forEach(j -> {
                if (!expected.isMissing(j)) {
                    Attribute attribute = expected.attribute(j);
                    switch (attribute.type()) {
                        case Attribute.DATE:
                        case Attribute.NOMINAL:
                            assertThat(actual.get(j)).isEqualTo(expected.stringValue(j));
                            break;
                        case Attribute.NUMERIC:
                            assertThat(actual.get(j)).isEqualTo(String.valueOf(expected.value(j)));
                            break;
                        default:
                            throw new IllegalStateException(
                                    String.format("Unexpected attribute [%s] type: %d!", attribute.name(),
                                            attribute.type()));
                    }
                } else {
                    assertThat(actual.get(j)).isNull();
                }
            });
        });
    }
}
