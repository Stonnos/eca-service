package com.ecaservice.data.storage.model.report;

import lombok.Data;

/**
 * Report properties.
 *
 * @author Roman Batygin
 */
@Data
public class ReportProperties {

    /**
     * Report title
     */
    private String title;

    /**
     * Report type
     */
    private ReportType reportType;

    /**
     * Is report enabled?
     */
    private Boolean enabled;
}
