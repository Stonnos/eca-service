package com.ecaservice.data.storage.model.report;

/**
 * Report type enum.
 *
 * @author Roman Batygin
 */
public enum ReportType {

    /**
     * Xls report type.
     */
    XLS {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitXls();
        }
    },

    /**
     * CSV report type.
     */
    CSV {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitCsv();
        }
    },

    /**
     * Arff report type.
     */
    ARFF {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitArff();
        }
    },

    /**
     * JSON report type.
     */
    JSON {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitJson();
        }
    },

    /**
     * XML report type.
     */
    XML {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitXml();
        }
    },

    /**
     * Txt report type.
     */
    TXT {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitTxt();
        }
    },

    /**
     * DATA report type.
     */
    DATA {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitData();
        }
    },

    /**
     * Docx report type.
     */
    DOCX {
        @Override
        public <T> T handle(ReportTypeVisitor<T> reportTypeVisitor) {
            return reportTypeVisitor.visitDocx();
        }
    };

    /**
     * Visitor pattern common method.
     *
     * @param reportTypeVisitor - report type visitor
     * @param <T>               - data generic type
     * @return result data
     */
    public abstract <T> T handle(ReportTypeVisitor<T> reportTypeVisitor);
}
