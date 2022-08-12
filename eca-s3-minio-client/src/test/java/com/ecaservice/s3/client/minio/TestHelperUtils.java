package com.ecaservice.s3.client.minio;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;

import java.io.Serializable;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    /**
     * Test object class.
     */
    @Data
    @Builder
    public static class TestObject implements Serializable {

        private int intValue;
        private double doubleValue;
    }
}
