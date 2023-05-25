package com.ecaservice.server.report.model;

import lombok.RequiredArgsConstructor;

/**
 * Base report type.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum BaseReportType {

    /**
     * Experiments list report
     */
    EXPERIMENTS("Отчет по заявкам на эксперимент"),

    /**
     * Evaluation logs list report
     */
    EVALUATION_LOGS("Отчет по классификаторам"),

    /**
     * Classifier options requests report
     */
    CLASSIFIERS_OPTIONS_REQUESTS("Отчет по оптимальным настройкам классификаторов");

    private final String description;

    /**
     * ERS report status description.
     *
     * @return ERS report status status description
     */
    public String getDescription() {
        return description;
    }
}
