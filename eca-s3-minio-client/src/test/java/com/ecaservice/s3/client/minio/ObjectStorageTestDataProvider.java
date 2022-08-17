package com.ecaservice.s3.client.minio;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

/**
 * Test data provider.
 *
 * @author Roman Batygin
 */
public class ObjectStorageTestDataProvider implements ArgumentsProvider {

    private static final double DOUBLE_VALUE = 1.2d;
    private static final int INT_VALUE = 5;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
        return Stream.of(testObjectArguments());
    }

    public Arguments testObjectArguments() {
        return Arguments.of(
                TestHelperUtils.TestObject.builder()
                        .intValue(INT_VALUE)
                        .doubleValue(DOUBLE_VALUE)
                        .build()
        );
    }
}
