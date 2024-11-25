package com.ecaservice.data.loader.validation;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

/**
 * Instances test data provider.
 *
 * @author Roman Batygin
 */
public class InstancesTestDataProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(
                Arguments.of("data/duplicate-nominal-values-dataset.json"),
                Arguments.of("data/empty-relation-name-dataset.json"),
                Arguments.of("data/empty-attributes-dataset.json"),
                Arguments.of("data/empty-dataset.json"),
                Arguments.of("data/empty-class-name-dataset.json"),
                Arguments.of("data/invalid-nominal-value.json"),
                Arguments.of("data/invalid-instance-row-size.json"),
                Arguments.of("data/empty-attr-name-dataset.json"),
                Arguments.of("data/empty-attr-type-dataset.json"),
                Arguments.of("data/empty-nominal-attr-values-dataset.json"),
                Arguments.of("data/invalid-date-attr-format-dataset.json"),
                Arguments.of("data/invalid-class-attribute-dataset.json"),
                Arguments.of("data/low-class-values-dataset.json"),
                Arguments.of("data/invalid-class-type-dataset.json"),
                Arguments.of("data/invalid-nominal-value-as-double-code.json")
        );
    }
}
