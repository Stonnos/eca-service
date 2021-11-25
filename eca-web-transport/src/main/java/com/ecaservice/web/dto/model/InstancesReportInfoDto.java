package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

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
    @Schema(description = "Report type", example = "XLS", maxLength = MAX_LENGTH_255)
    private String reportType;

    /**
     * Report title
     */
    @Schema(description = "Report title", example = "Microsoft Excel (.xlsx)", maxLength = MAX_LENGTH_255)
    private String title;

    /**
     * Report file extension
     */
    @Schema(description = "Report file extension", example = "xlsx", maxLength = MAX_LENGTH_255)
    private String fileExtension;
}
