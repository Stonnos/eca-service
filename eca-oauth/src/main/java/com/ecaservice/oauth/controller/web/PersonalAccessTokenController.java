package com.ecaservice.oauth.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.oauth.dto.CreatePersonalAccessTokenDto;
import com.ecaservice.oauth.service.PersonalAccessTokenService;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PersonalAccessTokenDetailsDto;
import com.ecaservice.web.dto.model.PersonalAccessTokenDto;
import com.ecaservice.web.dto.model.PersonalAccessTokensPageDto;
import com.ecaservice.web.dto.model.SimplePageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;

/**
 * Implements personal access token REST API.
 *
 * @author Roman Batygin
 */
@Slf4j
@Tag(name = "Personal access token API")
@Validated
@RestController
@RequestMapping("/personal-access-token")
@RequiredArgsConstructor
public class PersonalAccessTokenController {

    private final PersonalAccessTokenService personalAccessTokenService;

    /**
     * Creates personal access token for user.
     *
     * @param principal                    - principal object
     * @param createPersonalAccessTokenDto - personal access token
     * @return personal access token details
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Creates personal access token for user",
            summary = "Creates personal access token for user",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "CreatePersonalAccessToken",
                                    ref = "#/components/examples/CreatePersonalAccessToken"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "PersonalAccessTokenDetails",
                                                    ref = "#/components/examples/PersonalAccessTokenDetails"
                                            ),
                                    },
                                    schema = @Schema(implementation = PersonalAccessTokenDetailsDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DuplicatePersonalAccessTokenName",
                                                    ref = "#/components/examples/DuplicatePersonalAccessTokenName"
                                            ),
                                    }
                            )
                    )
            }
    )
    @PostMapping(value = "/create")
    public PersonalAccessTokenDetailsDto createToken(Principal principal,
                                                     @Valid @RequestBody
                                                     CreatePersonalAccessTokenDto createPersonalAccessTokenDto) {
        log.info("Received create personal access token request for user [{}]", principal.getName());
        return personalAccessTokenService.createToken(principal.getName(), createPersonalAccessTokenDto);
    }

    /**
     * Removes user personal access token.
     *
     * @param id - token id
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Removes user personal access token",
            summary = "Removes user personal access token",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DataNotFoundResponse",
                                                    ref = "#/components/examples/DataNotFoundResponse"
                                            ),
                                    }
                            )
                    )
            }
    )
    @DeleteMapping(value = "/remove/{id}")
    public void removeToken(@PathVariable Long id) {
        log.info("Received remove personal access token [{}] request", id);
        personalAccessTokenService.removeToken(id);
    }

    /**
     * Gets current user personal access tokens next page for specified page request.
     *
     * @param pageRequestDto - page request dto
     * @return personal access tokens page
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets current user personal access tokens next page for specified page request",
            summary = "Gets current user personal access tokens next page for specified page request",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "PageRequest",
                                    ref = "#/components/examples/PageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "PersonalAccessTokensPage",
                                                    ref = "#/components/examples/PersonalAccessTokensPage"
                                            ),
                                    },
                                    schema = @Schema(implementation = PersonalAccessTokensPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "BadPageRequestResponse",
                                                    ref = "#/components/examples/BadPageRequestResponse"
                                            ),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/list")
    public PageDto<PersonalAccessTokenDto> getTokens(Principal principal,
                                                     @Valid @RequestBody SimplePageRequestDto pageRequestDto) {
        log.info("Received user [{}] personal access tokens page request: {}", principal.getName(), pageRequestDto);
        return personalAccessTokenService.getNextPage(pageRequestDto, principal.getName());
    }
}
