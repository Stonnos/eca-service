package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

import static com.ecaservice.web.dto.util.FieldConstraints.READ_NOTIFICATIONS_LIST_MAX_LENGTH;

/**
 * Read notifications dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Read notifications model")
public class ReadNotificationsDto {

    /**
     * Notifications ids for current user. If ids is empty then all not read notifications will be read for current
     * user for last N days.
     */
    @Valid
    @Size(max = READ_NOTIFICATIONS_LIST_MAX_LENGTH)
    @Schema(description = "Notifications ids for current user. If ids is empty then all not read notifications will " +
            "be read for current user for last N days")
    private Set<@NotNull Long> ids;
}
