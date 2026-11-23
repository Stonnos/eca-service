package com.ecaservice.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Time series statistics filter.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Time series statistics filter")
public class TimeSeriesStatisticsFilter {

    /**
     * Predefined relative time period
     */
    @Schema(description = "Predefined time period", example = "MONTH")
    @NotNull
    private TimePeriod period;

    /**
     * Specific date for daily statistics (overrides period if specified)
     */
    @Schema(description = "Specific date for daily statistics (overrides period)", example = "2026-11-20")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate day;

    /**
     * Grouping granularity for aggregated data
     */
    @Schema(description = "Grouping granularity", example = "DAY", defaultValue = "DAY")
    @NotNull
    private TimePeriodGroupBy groupBy = TimePeriodGroupBy.DAY;
}
