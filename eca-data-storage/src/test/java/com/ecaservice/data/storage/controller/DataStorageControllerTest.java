package com.ecaservice.data.storage.controller;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.DataStorageException;
import com.ecaservice.data.storage.exception.EntityNotFoundException;
import com.ecaservice.data.storage.mapping.InstancesMapperImpl;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.StorageService;
import com.ecaservice.web.dto.model.CreateInstancesResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MimeTypeUtils;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;

import static com.ecaservice.data.storage.TestHelperUtils.createInstancesEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class DataStorageControllerTest {

    private static final String BASE_URL = "/instances";
    private static final String SAVE_URL = BASE_URL + "/save";
    private static final String RENAME_URL = BASE_URL + "/rename";
    private static final String DELETE_URL = BASE_URL + "/delete";

    private static final String TRAINING_DATA_PARAM = "trainingData";
    private static final String TABLE_NAME = "table";
    private static final String TABLE_NAME_PARAM = "tableName";
    private static final String ERROR_MESSAGE = "Error";
    private static final String ID_PARAM = "id";
    private static final long ID = 1L;
    private static final String NEW_NAME_PARAM = "newName";

    @MockBean
    private StorageService storageService;
    @MockBean
    private InstancesRepository instancesRepository;

    @Inject
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final MockMultipartFile trainingData =
            new MockMultipartFile(TRAINING_DATA_PARAM, "iris.txt",
                    MimeTypeUtils.TEXT_PLAIN.toString(), "file-content".getBytes(StandardCharsets.UTF_8));

    @Test
    void testSaveInstances() throws Exception {
        InstancesEntity instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(TABLE_NAME);
        when(storageService.saveData(any(), anyString())).thenReturn(instancesEntity);
        CreateInstancesResultDto expected = new CreateInstancesResultDto();
        expected.setId(instancesEntity.getId());
        expected.setTableName(instancesEntity.getTableName());
        expected.setSourceFileName(trainingData.getOriginalFilename());
        expected.setCreated(true);
        mockMvc.perform(multipart(SAVE_URL)
                .file(trainingData)
                .param(TABLE_NAME_PARAM, TABLE_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testSaveInstancesWithError() throws Exception {
        InstancesEntity instancesEntity = createInstancesEntity();
        instancesEntity.setTableName(TABLE_NAME);
        when(storageService.saveData(any(), anyString())).thenThrow(new DataStorageException(ERROR_MESSAGE));
        CreateInstancesResultDto expected = new CreateInstancesResultDto();
        expected.setTableName(instancesEntity.getTableName());
        expected.setSourceFileName(trainingData.getOriginalFilename());
        expected.setCreated(false);
        expected.setErrorMessage(ERROR_MESSAGE);
        mockMvc.perform(multipart(SAVE_URL)
                .file(trainingData)
                .param(TABLE_NAME_PARAM, TABLE_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testSaveExistingInstances() throws Exception {
        when(instancesRepository.existsByTableName(anyString())).thenReturn(true);
        mockMvc.perform(multipart(SAVE_URL)
                .file(trainingData)
                .param(TABLE_NAME_PARAM, TABLE_NAME))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRenameData() throws Exception {
        mockMvc.perform(put(RENAME_URL)
                .param(ID_PARAM, String.valueOf(ID))
                .param(NEW_NAME_PARAM, TABLE_NAME)).andExpect(status().isOk());
    }

    @Test
    void testRenameDataWithExistingTableName() throws Exception {
        when(instancesRepository.existsByTableName(anyString())).thenReturn(true);
        mockMvc.perform(put(RENAME_URL)
                .param(ID_PARAM, String.valueOf(ID))
                .param(NEW_NAME_PARAM, TABLE_NAME)).andExpect(status().isBadRequest());
    }

    @Test
    void testRenameDataNotFound() throws Exception {
        doThrow(new EntityNotFoundException()).when(storageService).renameData(anyLong(), anyString());
        mockMvc.perform(put(RENAME_URL)
                .param(ID_PARAM, String.valueOf(ID))
                .param(NEW_NAME_PARAM, TABLE_NAME)).andExpect(status().isNotFound());
    }


    @Test
    void testDeleteData() throws Exception {
        mockMvc.perform(delete(DELETE_URL)
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteDataNotFound() throws Exception {
        doThrow(new EntityNotFoundException()).when(storageService).deleteData(anyLong());
        mockMvc.perform(delete(DELETE_URL)
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isNotFound());
    }
}
