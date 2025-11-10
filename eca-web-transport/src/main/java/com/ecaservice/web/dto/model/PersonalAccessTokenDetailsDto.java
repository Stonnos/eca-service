package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;

/**
 * Personal access token details dto.
 *
 * @author Roman Batygin
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Personal access token details")
public class PersonalAccessTokenDetailsDto extends PersonalAccessTokenDto {

    /**
     * Token value
     */
    @Schema(description = "Token value", maxLength = MAX_LENGTH_255,
            example = "6Igz-7_3QGSioGgoFxSe_N_ByhCq1IuQaKJ2i_GBw5TZT_SVtfK44DThXfZ8jj0eXOHazAx23MwfUVpNkX-d2GVdguKlOPEJgXx_xfYAJ-8zOiUiar7H3b39w4RCKrdF")
    private String token;
}
