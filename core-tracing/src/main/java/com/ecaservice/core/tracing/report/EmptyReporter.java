package com.ecaservice.core.tracing.report;

import zipkin2.Span;
import zipkin2.reporter.Reporter;

/**
 * Implements empty reporter.
 *
 * @author Roman Batygin
 */
public class EmptyReporter implements Reporter<Span> {
    @Override
    public void report(Span span) {
    }
}
