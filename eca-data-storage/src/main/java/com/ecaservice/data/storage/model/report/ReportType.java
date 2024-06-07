package com.ecaservice.data.storage.model.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Report type enum.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum ReportType {

    /**
     * Xls report type.
     */
    XLS("xlsx") {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitXls();
        }
    },

    /**
     * CSV report type.
     */
    CSV("csv") {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitCsv();
        }
    },

    /**
     * Arff report type.
     */
    ARFF("arff") {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitArff();
        }
    },

    /**
     * JSON report type.
     */
    JSON("json") {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitJson();
        }
    },

    /**
     * Txt report type.
     */
    TXT("txt") {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitTxt();
        }
    },

    /**
     * DATA report type.
     */
    DATA("data") {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitData();
        }
    };

    /**
     * Report extension
     */
    private final String extension;

    /**
     * Visitor pattern common method.
     *
     * @param reportTypeVisitor - report type visitor
     * @param <T>               - data generic type
     * @return result data
     */
    public abstract <T> T handle(ReportTypeVisitor<T> reportTypeVisitor);
}
