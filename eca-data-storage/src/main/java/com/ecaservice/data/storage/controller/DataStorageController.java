package com.ecaservice.data.storage.controller;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.mapping.InstancesMapper;
import com.ecaservice.data.storage.model.MultipartFileResource;
import com.ecaservice.data.storage.model.report.ReportType;
import com.ecaservice.data.storage.report.InstancesReportService;
import com.ecaservice.data.storage.service.InstancesLoader;
import com.ecaservice.data.storage.service.StorageService;
import com.ecaservice.web.dto.model.CreateInstancesResultDto;
import com.ecaservice.web.dto.model.InstancesDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import weka.core.Instances;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.data.storage.controller.doc.ApiExamples.SIMPLE_PAGE_REQUEST_JSON;

/**
 * Data storage API for web application.
 *
 * @author Roman Batygin
 */
@Validated
@Slf4j
@Tag(name = "Data storage API for web application")
@RestController
@RequestMapping("/instances")
@RequiredArgsConstructor
public class DataStorageController {

    private static final String TABLE_NAME_REGEX = "^[0-9a-z_]+$";
    private static final int MAX_TABLE_NAME_LENGTH = 30;

    private final StorageService storageService;
    private final InstancesReportService instancesReportService;
    private final InstancesLoader instancesLoader;
    private final InstancesMapper instancesMapper;

    /**
     * Finds instances tables with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return instances tables page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds instances tables with specified options such as filter, sorting and paging",
            summary = "Finds instances tables with specified options such as filter, sorting and paging",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = SIMPLE_PAGE_REQUEST_JSON)
                    })
            })
    )
    @PostMapping(value = "/list")
    public PageDto<InstancesDto> getInstancesPage(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received instances page request: {}", pageRequestDto);
        Page<InstancesEntity> instancesPage = storageService.getNextPage(pageRequestDto);
        List<InstancesDto> instancesDtoList = instancesMapper.map(instancesPage.getContent());
        return PageDto.of(instancesDtoList, pageRequestDto.getPage(), instancesPage.getTotalElements());
    }

    /**
     * Saves instances into database.
     *
     * @param trainingData - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param tableName    - table name
     * @return create instances results dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Saves instances into database",
            summary = "Saves instances into database",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreateInstancesResultDto saveInstances(
            @Parameter(description = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            @Parameter(description = "Table name", required = true)
            @Pattern(regexp = TABLE_NAME_REGEX)
            @Size(max = MAX_TABLE_NAME_LENGTH) @RequestParam String tableName) {
        log.info("Received request for saving instances '{}' into table [{}]",
                trainingData.getOriginalFilename(), tableName);
        MultipartFileResource multipartFileResource = new MultipartFileResource(trainingData);
        Instances instances = instancesLoader.load(multipartFileResource);
        InstancesEntity instancesEntity = storageService.saveData(instances, tableName);
        return CreateInstancesResultDto.builder()
                .id(instancesEntity.getId())
                .sourceFileName(trainingData.getOriginalFilename())
                .tableName(instancesEntity.getTableName())
                .build();
    }

    /**
     * Gets instances details.
     *
     * @param id - instances id
     * @return instances dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets instances details",
            summary = "Gets instances details",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/details/{id}")
    public InstancesDto getInstancesDetails(@Parameter(description = "Instances id", example = "1", required = true)
                                            @PathVariable Long id) {
        log.info("Request get instances [{}] details", id);
        var instancesEntity = storageService.getById(id);
        return instancesMapper.map(instancesEntity);
    }

    /**
     * Renames data with specified id.
     *
     * @param id        - instances id
     * @param tableName - new table name
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Renames data with specified id",
            summary = "Renames data with specified id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @PutMapping(value = "/rename")
    public void rename(@Parameter(description = "Instances id", example = "1", required = true) @RequestParam long id,
                       @Parameter(description = "Table name", required = true)
                       @Pattern(regexp = TABLE_NAME_REGEX)
                       @Size(max = MAX_TABLE_NAME_LENGTH) @RequestParam String tableName) {
        storageService.renameData(id, tableName);
    }

    /**
     * Deletes instances with specified id.
     *
     * @param id - instances id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Deletes instances with specified id",
            summary = "Deletes instances with specified id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @DeleteMapping(value = "/delete")
    public void delete(@Parameter(description = "Instances id", example = "1", required = true) @RequestParam long id) {
        storageService.deleteData(id);
    }

    /**
     * Finds data page for specified instances.
     *
     * @param id             - instances id
     * @param pageRequestDto - page request dto
     * @return response entity
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds data page for specified instances",
            summary = "Finds data page for specified instances",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = SIMPLE_PAGE_REQUEST_JSON)
                    })
            })
    )
    @PostMapping(value = "/data-page")
    public PageDto<List<String>> getDataPage(
            @Parameter(description = "Instances id", example = "1", required = true)
            @RequestParam long id,
            @Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received data page request: {}, instances id [{}]", pageRequestDto, id);
        return storageService.getData(id, pageRequestDto);
    }

    /**
     * Gets attributes list for specified instances.
     *
     * @param id - instances id
     * @return attributes list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets attributes list for specified instances",
            summary = "Gets attributes list for specified instances",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/attributes/{id}")
    public List<String> getAttributes(@Parameter(description = "Instances id", example = "1", required = true)
                                      @PathVariable Long id) {
        log.info("Received attributes request for instances [{}]", id);
        return storageService.getAttributes(id);
    }

    /**
     * Download instances report with specified type.
     *
     * @param id                  - instances id
     * @param reportType          - report type
     * @param httpServletResponse - http servlet response
     * @throws Exception in case of error
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Download instances report with specified type",
            summary = "Download instances report with specified type",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/download")
    public void downloadInstancesReport(
            @Parameter(description = "Instances id", example = "1", required = true)
            @RequestParam long id,
            @Parameter(description = "Report type", required = true)
            @RequestParam ReportType reportType,
            HttpServletResponse httpServletResponse) throws Exception {
        log.info("Request to download instances [{}] report [{}]", id, reportType);
        instancesReportService.generateInstancesReport(id, reportType, httpServletResponse);
    }
}
