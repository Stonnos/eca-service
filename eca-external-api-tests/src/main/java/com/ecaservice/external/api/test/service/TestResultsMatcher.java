package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.test.entity.MatchResult;
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
     * @return match result
     */
    public MatchResult compareAndMatch(Object expected, Object actual) {
        MatchResult matchResult = MatchResult.MATCH;
        if (expected != null && StringUtils.isNotEmpty(String.valueOf(expected)) &&
                (actual == null || StringUtils.isEmpty(String.valueOf(actual)))) {
            ++totalNotFound;
            matchResult = MatchResult.NOT_FOUND;
        } else if (!Objects.equals(expected, actual)) {
            ++totalNotMatched;
            matchResult = MatchResult.NOT_MATCH;
        } else {
            ++totalMatched;
        }
        return matchResult;
    }
}
