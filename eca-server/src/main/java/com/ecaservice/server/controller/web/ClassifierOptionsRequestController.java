package com.ecaservice.server.controller.web;

import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.controller.doc.ApiExamples;
import com.ecaservice.server.service.ers.ClassifierOptionsRequestService;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;

/**
 * Classifier options requests API for web application.
 *
 * @author Roman Batygin
 */
@Tag(name = "Classifier options requests API for web application")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ClassifierOptionsRequestController {

    private final ClassifierOptionsRequestService classifierOptionsRequestService;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;

    /**
     * Finds classifiers options requests models with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return classifiers options requests models page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds classifiers options requests models with specified options",
            summary = "Finds classifiers options requests models with specified options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = ApiExamples.CLASSIFIER_OPTIONS_REQUESTS_PAGE_REQUEST_JSON)
                    })
            })
    )
    @PostMapping(value = "/classifiers-options-requests")
    public PageDto<ClassifierOptionsRequestDto> getClassifierOptionsRequestModels(
            @Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received classifiers options requests models page request: {}", pageRequestDto);
        Page<ClassifierOptionsRequestModel> classifierOptionsRequestModelPage =
                classifierOptionsRequestService.getNextPage(pageRequestDto);
        List<ClassifierOptionsRequestDto> classifierOptionsRequestDtoList =
                classifierOptionsRequestModelMapper.map(classifierOptionsRequestModelPage.getContent());
        return PageDto.of(classifierOptionsRequestDtoList, pageRequestDto.getPage(),
                classifierOptionsRequestModelPage.getTotalElements());
    }
}
