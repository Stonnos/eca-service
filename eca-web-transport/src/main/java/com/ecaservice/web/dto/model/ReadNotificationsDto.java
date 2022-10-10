package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.READ_NOTIFICATIONS_LIST_MAX_LENGTH;

/**
 * Read notifications dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Read notifications model")
public class ReadNotificationsDto {

    /**
     * Notifications ids
     */
    @Valid
    @Size(max = READ_NOTIFICATIONS_LIST_MAX_LENGTH)
    @Schema(description = "Notifications ids")
    private List<@NotNull Long> ids;
}
