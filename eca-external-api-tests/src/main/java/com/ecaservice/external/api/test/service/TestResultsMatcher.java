package com.ecaservice.external.api.test.service;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Test results matcher.
 *
 * @author Roman Batygin
 */
@Getter
public class TestResultsMatcher {

    private int totalNotMatched;
    private int totalMatched;
    private int totalNotFound;

    /**
     * Compare and match values.
     *
     * @param expected - expected value
     * @param actual   - actual value
     */
    public void compareMatchAndReport(Object expected, Object actual) {
        if (expected != null && StringUtils.isNotEmpty(String.valueOf(expected)) &&
                (actual == null || StringUtils.isEmpty(String.valueOf(actual)))) {
            ++totalNotFound;
        } else if (!Objects.equals(expected, actual)) {
            ++totalNotMatched;
        } else {
            ++totalMatched;
        }
    }
}
