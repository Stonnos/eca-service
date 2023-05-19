package com.ecaservice.data.storage;

import lombok.experimental.UtilityClass;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Assertion utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class AssertionUtils {

    /**
     * Asserts instances. Compare all values.
     *
     * @param expected - expected instances
     * @param actual   - actual instances
     */
    public static void assertInstances(Instances expected, Instances actual) {
        assertNotNull(actual);
        assertEquals(expected.numAttributes(), actual.numAttributes());
        assertEquals(expected.numInstances(), actual.numInstances());
        assertEquals(expected.numClasses(), actual.numClasses());
        IntStream.range(0, expected.numInstances()).forEach(i -> {
            Instance expectedInstance = expected.instance(i);
            Instance actualInstance = actual.instance(i);
            IntStream.range(0, expectedInstance.numAttributes()).forEach(j -> {
                Attribute attribute = expectedInstance.attribute(j);
                switch (attribute.type()) {
                    case Attribute.NOMINAL:
                    case Attribute.DATE:
                        assertEquals(expectedInstance.stringValue(j), actualInstance.stringValue(j));
                        break;
                    case Attribute.NUMERIC:
                        assertEquals(expectedInstance.value(j), actualInstance.value(j));
                        break;
                    default:
                        fail("Expected numeric or nominal attribute type!");
                }
            });
        });
    }

    /**
     * Asserts instances data list. Compare all values.
     *
     * @param instances - instances
     * @param dataList  - data list
     */
    public static void assertDataList(Instances instances, List<List<String>> dataList) {
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
