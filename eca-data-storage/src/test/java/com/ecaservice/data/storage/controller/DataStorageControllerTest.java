package com.ecaservice.data.storage.controller;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.TableExistsException;
import com.ecaservice.data.storage.mapping.InstancesMapper;
import com.ecaservice.data.storage.mapping.InstancesMapperImpl;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.StorageServiceImpl;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.CreateInstancesResultDto;
import com.ecaservice.web.dto.model.InstancesDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.file.FileDataLoader;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MimeTypeUtils;
import weka.core.Instances;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.data.storage.TestHelperUtils.bearerHeader;
import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.data.storage.TestHelperUtils.loadInstances;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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

    private static final String BASE_URL = "/instances";
    private static final String SAVE_URL = BASE_URL + "/save";
    private static final String RENAME_URL = BASE_URL + "/rename";
    private static final String DELETE_URL = BASE_URL + "/delete";
    private static final String LIST_URL = BASE_URL + "/list";

    private static final String TRAINING_DATA_PARAM = "trainingData";
    private static final String TABLE_NAME = "table";
    private static final String TABLE_NAME_PARAM = "tableName";
    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";

    private static final String ERROR_MESSAGE = "Error";
    private static final String ID_PARAM = "id";
    private static final long ID = 1L;
    private static final long TOTAL_ELEMENTS = 1L;
    private static final int PAGE_NUMBER = 0;

    @MockBean
    private StorageServiceImpl storageService;
    @MockBean
    private InstancesRepository instancesRepository;
    @MockBean
    private FileDataLoader fileDataLoader;
    @MockBean
    private JdbcTemplate jdbcTemplate;

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
        when(fileDataLoader.loadInstances()).thenReturn(instances);
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        when(storageService.saveData(any(Instances.class), anyString())).thenReturn(instancesEntity);
        CreateInstancesResultDto expected = new CreateInstancesResultDto();
        expected.setId(instancesEntity.getId());
        expected.setTableName(instancesEntity.getTableName());
        expected.setSourceFileName(trainingData.getOriginalFilename());
        expected.setCreated(true);
        mockMvc.perform(multipart(SAVE_URL)
                .file(trainingData)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(TABLE_NAME_PARAM, TABLE_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testSaveInstancesWithError() throws Exception {
        Instances instances = loadInstances();
        InstancesEntity instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(TABLE_NAME);
        when(fileDataLoader.loadInstances()).thenReturn(instances);
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        when(storageService.saveData(any(Instances.class), anyString())).thenThrow(
                new IllegalStateException(ERROR_MESSAGE));
        CreateInstancesResultDto expected = new CreateInstancesResultDto();
        expected.setTableName(instancesEntity.getTableName());
        expected.setSourceFileName(trainingData.getOriginalFilename());
        expected.setCreated(false);
        expected.setErrorMessage(ERROR_MESSAGE);
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
        when(fileDataLoader.loadInstances()).thenReturn(instances);
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
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class))).thenReturn(true);
        mockMvc.perform(put(RENAME_URL)
                .param(ID_PARAM, String.valueOf(ID))
                .param(TABLE_NAME_PARAM, TABLE_NAME)).andExpect(status().isUnauthorized());
    }

    @Test
    void testRenameData() throws Exception {
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class))).thenReturn(true);
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
        mockMvc.perform(get(LIST_URL)
                .param(PAGE_PARAM, String.valueOf(PAGE_NUMBER))
                .param(SIZE_PARAM, String.valueOf(TOTAL_ELEMENTS)))
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
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_PARAM, String.valueOf(PAGE_NUMBER))
                .param(SIZE_PARAM, String.valueOf(TOTAL_ELEMENTS)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }
}
