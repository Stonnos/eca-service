package com.ecaservice.oauth.controller.internal;

import com.ecaservice.oauth.service.PersonalAccessTokenService;
import com.ecaservice.user.dto.PersonalAccessTokenInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Implements internal API for personal access tokens.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Tag(name = "Internal personal access tokens API")
@RestController
@RequestMapping("/api/internal/personal-access-token")
@RequiredArgsConstructor
public class PersonalAccessTokenApiController {

    private final PersonalAccessTokenService personalAccessTokenService;

    /**
     * Verify personal access token.
     *
     * @param token - token value
     * @return {@code true} if token is valid (not expired). {@code false} otherwise
     */
    @PreAuthorize("hasAuthority('SCOPE_internal-api')")
    @Operation(
            description = "Verify personal access token",
            summary = "Verify personal access token",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "PersonalAccessTokenInfoResponse",
                                                    ref = "#/components/examples/PersonalAccessTokenInfoResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = PersonalAccessTokenInfoDto.class)
                            )
                    )
            }
    )
    @GetMapping(value = "/verify-token")
    public PersonalAccessTokenInfoDto verifyToken(
            @Size(min = VALUE_1, max = MAX_LENGTH_255)
            @Parameter(description = "Personal access token", required = true) @RequestParam String token) {
        log.info("Received request to verify personal access token");
        return personalAccessTokenService.verifyToken(token);
    }
}
