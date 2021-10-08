package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Instances report info model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Instances report info model")
public class InstancesReportInfoDto {

    /**
     * Report type
     */
    @Schema(description = "Report type", example = "XLS")
    private String reportType;

    /**
     * Report title
     */
    @Schema(description = "Report title", example = "Microsoft Excel (.xlsx)")
    private String title;

    /**
     * Report file extension
     */
    @Schema(description = "Report file extension", example = "xlsx")
    private String fileExtension;
}
