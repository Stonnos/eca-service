package com.ecaservice.data.storage.controller.web;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.TableExistsException;
import com.ecaservice.data.storage.mapping.InstancesMapper;
import com.ecaservice.data.storage.mapping.InstancesMapperImpl;
import com.ecaservice.data.storage.model.MultipartFileResource;
import com.ecaservice.data.storage.model.report.ReportType;
import com.ecaservice.data.storage.report.InstancesReportService;
import com.ecaservice.data.storage.report.ReportsConfigurationService;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.AttributeService;
import com.ecaservice.data.storage.service.InstancesLoader;
import com.ecaservice.data.storage.service.impl.StorageServiceImpl;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.AttributeDto;
import com.ecaservice.web.dto.model.CreateInstancesResultDto;
import com.ecaservice.web.dto.model.InstancesDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MimeTypeUtils;
import weka.core.Instances;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.data.storage.TestHelperUtils.bearerHeader;
import static com.ecaservice.data.storage.TestHelperUtils.createAttributeDto;
import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.data.storage.TestHelperUtils.createPageRequestDto;
import static com.ecaservice.data.storage.TestHelperUtils.createReportProperties;
import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link DataStorageController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = DataStorageController.class)
@Import(InstancesMapperImpl.class)
class DataStorageControllerTest extends AbstractControllerTest {

    private static final List<AttributeDto> EXPECTED_ATTRIBUTES = List.of(
            createAttributeDto("attr1"),
            createAttributeDto("attr2")
    );
    private static final List<String> VALUES = List.of("val1", "val2");

    private static final String BASE_URL = "/instances";
    private static final String SAVE_URL = BASE_URL + "/save";
    private static final String RENAME_URL = BASE_URL + "/rename";
    private static final String SET_CLASS_URL = BASE_URL + "/set-class-attribute";
    private static final String SELECT_ATTRIBUTE_URL = BASE_URL + "/select-attribute";
    private static final String UNSELECT_ATTRIBUTE_URL = BASE_URL + "/unselect-attribute";
    private static final String SELECT_ALL_ATTRIBUTES_URL = BASE_URL + "/select-all-attributes";
    private static final String DELETE_URL = BASE_URL + "/delete";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String ATTRIBUTES_URL = BASE_URL + "/attributes/{id}";
    private static final String DATA_PAGE_URL = BASE_URL + "/data-page";
    private static final String DETAILS_URL = BASE_URL + "/details/{id}";
    private static final String DOWNLOAD_REPORT_URL = BASE_URL + "/download";
    private static final String REPORTS_INFO_URL = BASE_URL + "/reports-info";

    private static final String TRAINING_DATA_PARAM = "trainingData";
    private static final String TABLE_NAME = "table";
    private static final String TABLE_NAME_PARAM = "tableName";

    private static final String ID_PARAM = "id";
    private static final String REPORT_TYPE_PARAM = "reportType";
    private static final long ID = 1L;
    private static final long TOTAL_ELEMENTS = 1L;
    private static final int PAGE_NUMBER = 0;
    private static final String CLASS_ATTRIBUTE_ID_PARAM = "classAttributeId";

    @MockBean
    private StorageServiceImpl storageService;
    @MockBean
    private InstancesRepository instancesRepository;
    @MockBean
    private InstancesLoader instancesLoader;
    @MockBean
    private InstancesReportService instancesReportService;
    @MockBean
    private AttributeService attributeService;
    @MockBean
    private ReportsConfigurationService reportsConfigurationService;

    @Inject
    private InstancesMapper instancesMapper;

    private final MockMultipartFile trainingData =
            new MockMultipartFile(TRAINING_DATA_PARAM, "iris.txt",
                    MimeTypeUtils.TEXT_PLAIN.toString(), "file-content".getBytes(StandardCharsets.UTF_8));

    @Test
    void testSaveInstancesUnauthorized() throws Exception {
        mockMvc.perform(multipart(SAVE_URL)
                .file(trainingData)
                .param(TABLE_NAME_PARAM, TABLE_NAME))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSaveInstances() throws Exception {
        Instances instances = loadInstances();
        InstancesEntity instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(TABLE_NAME);
        when(instancesLoader.load(any(MultipartFileResource.class))).thenReturn(instances);
        when(storageService.saveData(any(Instances.class), anyString())).thenReturn(instancesEntity);
        CreateInstancesResultDto expected = CreateInstancesResultDto.builder()
                .id(instancesEntity.getId())
                .uuid(instancesEntity.getUuid())
                .tableName(instancesEntity.getTableName())
                .sourceFileName(trainingData.getOriginalFilename())
                .build();
        mockMvc.perform(multipart(SAVE_URL)
                .file(trainingData)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(TABLE_NAME_PARAM, TABLE_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testSaveExistingInstances() throws Exception {
        Instances instances = loadInstances();
        when(instancesLoader.load(any(MultipartFileResource.class))).thenReturn(instances);
        when(storageService.saveData(any(Instances.class), anyString())).thenThrow(
                new TableExistsException(TABLE_NAME));
        mockMvc.perform(multipart(SAVE_URL)
                .file(trainingData)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(TABLE_NAME_PARAM, TABLE_NAME))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRenameDataUnauthorized() throws Exception {
        mockMvc.perform(put(RENAME_URL)
                .param(ID_PARAM, String.valueOf(ID))
                .param(TABLE_NAME_PARAM, TABLE_NAME)).andExpect(status().isUnauthorized());
    }

    @Test
    void testRenameData() throws Exception {
        mockMvc.perform(put(RENAME_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(ID))
                .param(TABLE_NAME_PARAM, TABLE_NAME)).andExpect(status().isOk());
    }

    @Test
    void testRenameDataWithExistingTableName() throws Exception {
        doThrow(new TableExistsException(TABLE_NAME)).when(storageService).renameData(ID, TABLE_NAME);
        mockMvc.perform(put(RENAME_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(ID))
                .param(TABLE_NAME_PARAM, TABLE_NAME)).andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteDataUnauthorized() throws Exception {
        mockMvc.perform(delete(DELETE_URL)
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteData() throws Exception {
        mockMvc.perform(delete(DELETE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetInstancesPageUnauthorized() throws Exception {
        mockMvc.perform(post(LIST_URL)
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetInstancesPage() throws Exception {
        Page<InstancesEntity> instancesEntityPage = Mockito.mock(Page.class);
        when(instancesEntityPage.getTotalElements()).thenReturn(TOTAL_ELEMENTS);
        List<InstancesEntity> instancesDtoList = Collections.singletonList(createInstancesEntity());
        PageDto<InstancesDto> expected = PageDto.of(instancesMapper.map(instancesDtoList), PAGE_NUMBER, TOTAL_ELEMENTS);
        when(instancesEntityPage.getContent()).thenReturn(instancesDtoList);
        when(storageService.getNextPage(any(PageRequestDto.class))).thenReturn(instancesEntityPage);
        mockMvc.perform(post(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testGetAttributesUnauthorized() throws Exception {
        mockMvc.perform(get(ATTRIBUTES_URL, ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAttributes() throws Exception {
        when(storageService.getAttributes(ID)).thenReturn(EXPECTED_ATTRIBUTES);
        mockMvc.perform(get(ATTRIBUTES_URL, ID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(EXPECTED_ATTRIBUTES)));
    }

    @Test
    void testGetDataPageUnauthorized() throws Exception {
        mockMvc.perform(post(DATA_PAGE_URL)
                .param(ID_PARAM, String.valueOf(ID))
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetDataPage() throws Exception {
        var expected = PageDto.of(Collections.singletonList(VALUES), PAGE_NUMBER, TOTAL_ELEMENTS);
        when(storageService.getData(anyLong(), any(PageRequestDto.class))).thenReturn(expected);
        mockMvc.perform(post(DATA_PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(ID))
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testGetInstancesDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(DETAILS_URL, ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetInstancesDetails() throws Exception {
        var instancesEntity = createInstancesEntity();
        when(storageService.getById(ID)).thenReturn(instancesEntity);
        var expected = instancesMapper.map(instancesEntity);
        mockMvc.perform(get(DETAILS_URL, ID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testDownloadInstancesReportUnauthorized() throws Exception {
        mockMvc.perform(get(DOWNLOAD_REPORT_URL)
                .param(REPORT_TYPE_PARAM, ReportType.XLS.name())
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDownloadInstancesReportOk() throws Exception {
        mockMvc.perform(get(DOWNLOAD_REPORT_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(REPORT_TYPE_PARAM, ReportType.XLS.name())
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetInstancesReportsInfoUnauthorized() throws Exception {
        mockMvc.perform(get(REPORTS_INFO_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetInstancesReportsInfo() throws Exception {
        var reportPropertiesList = Collections.singletonList(createReportProperties());
        var expected = instancesMapper.mapReportPropertiesList(reportPropertiesList);
        when(reportsConfigurationService.getReportProperties()).thenReturn(reportPropertiesList);
        mockMvc.perform(get(REPORTS_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testSetClassUnauthorized() throws Exception {
        mockMvc.perform(put(SET_CLASS_URL)
                .param(CLASS_ATTRIBUTE_ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSetClass() throws Exception {
        mockMvc.perform(put(SET_CLASS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(CLASS_ATTRIBUTE_ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isOk());
    }

    @Test
    void testSetClassWithNullClassAttributeId() throws Exception {
        mockMvc.perform(put(SET_CLASS_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSelectAttributeUnauthorized() throws Exception {
        mockMvc.perform(put(SELECT_ATTRIBUTE_URL)
                        .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSelectAttribute() throws Exception {
        mockMvc.perform(put(SELECT_ATTRIBUTE_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                        .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isOk());
    }

    @Test
    void testSelectAttributeWithNullId() throws Exception {
        mockMvc.perform(put(SELECT_ATTRIBUTE_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUnselectAttributeUnauthorized() throws Exception {
        mockMvc.perform(put(UNSELECT_ATTRIBUTE_URL)
                        .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUnselectAttribute() throws Exception {
        mockMvc.perform(put(UNSELECT_ATTRIBUTE_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                        .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isOk());
    }

    @Test
    void testUnselectAttributeWithNullId() throws Exception {
        mockMvc.perform(put(UNSELECT_ATTRIBUTE_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSelectAllAttributesUnauthorized() throws Exception {
        mockMvc.perform(put(SELECT_ALL_ATTRIBUTES_URL)
                        .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSelectAllAttributes() throws Exception {
        mockMvc.perform(put(SELECT_ALL_ATTRIBUTES_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                        .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isOk());
    }

    @Test
    void testSelectAllAttributesWithNullId() throws Exception {
        mockMvc.perform(put(SELECT_ALL_ATTRIBUTES_URL)
                        .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }
}
