package com.ecaservice.web.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto model for charts.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartDataDto {

    /**
     * Chart item label
     */
    private String label;

    /**
     * Chart item value
     */
    private Long count;
}
