package com.ecaservice.controller.web;

import com.ecaservice.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.service.ers.ClassifierOptionsRequestService;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Classifier options requests API for web application.
 *
 * @author Roman Batygin
 */
@Api(tags = "Classifier options requests API for web application")
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
    @ApiOperation(
            value = "Finds classifiers options requests models with specified options",
            notes = "Finds classifiers options requests models with specified options"
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
