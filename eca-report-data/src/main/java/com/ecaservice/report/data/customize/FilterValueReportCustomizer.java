package com.ecaservice.report.data.customize;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Filter value report customizer.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class FilterValueReportCustomizer {

    private final String filterField;

    /**
     * Gets filter values as string.
     *
     * @param values - filter values
     * @return filter values string
     */
    public abstract String getFilterValuesAsString(List<String> values);
}
