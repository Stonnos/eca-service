package com.ecaservice.controller.web;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.exception.EntityNotFoundException;
import com.ecaservice.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.mapping.ClassifierOptionsDatabaseModelMapperImpl;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel_;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.model.options.LogisticOptions;
import com.ecaservice.service.classifiers.ClassifierOptionsService;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeTypeUtils;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.PageRequestUtils.FILTER_MATCH_MODE_PARAM;
import static com.ecaservice.PageRequestUtils.FILTER_NAME_PARAM;
import static com.ecaservice.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.PageRequestUtils.PAGE_NUMBER_PARAM;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE_PARAM;
import static com.ecaservice.PageRequestUtils.TOTAL_ELEMENTS;
import static com.ecaservice.TestHelperUtils.bearerHeader;
import static com.ecaservice.TestHelperUtils.createClassifiersConfiguration;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests fro checking {@link ClassifierOptionsController} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ClassifierOptionsController.class)
@Import(ClassifierOptionsDatabaseModelMapperImpl.class)
public class ClassifierOptionsControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/experiment/classifiers-options";
    private static final String ACTIVE_OPTIONS_URL = BASE_URL + "/active-options";
    private static final String PAGE_URL = BASE_URL + "/page";
    private static final String DELETE_URL = BASE_URL + "/delete";
    private static final String SAVE_URL = BASE_URL + "/save";

    private static final String CONFIGURATION_ID_PARAM = "configurationId";
    private static final String ID_PARAM = "id";
    private static final String CLASSIFIER_OPTIONS_FILE_PARAM = "classifiersOptionsFile";

    private static final String OPTIONS = "options";
    private static final long CONFIGURATION_ID = 1L;

    @MockBean
    private ClassifierOptionsService classifierOptionsService;
    
    @Inject
    private ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper;

    @Test
    public void testGetActiveOptionsUnauthorized() throws Exception {
        mockMvc.perform(get(ACTIVE_OPTIONS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetActiveOptionsOk() throws Exception {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels = Collections.singletonList(
                TestHelperUtils.createClassifierOptionsDatabaseModel(OPTIONS, classifiersConfiguration));
        List<ClassifierOptionsDto> classifierOptionsDtoList =
                classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModels);
        when(classifierOptionsService.getActiveClassifiersOptions()).thenReturn(classifierOptionsDatabaseModels);
        mockMvc.perform(get(ACTIVE_OPTIONS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(classifierOptionsDtoList)));
    }

    @Test
    public void testGetClassifiersOptionsPageUnauthorized() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetClassifiersOptionsPageWithNullConfigurationId() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifiersOptionsPageWithNullPageNumber() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifiersOptionsPageWithNullPageSize() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifiersOptionsPageWithZeroPageSize() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifiersOptionsPageWithNegativePageNumber() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_NUMBER_PARAM, String.valueOf(-1))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifiersOptionsPageWithEmptyFilterRequestName() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE))
                .param(FILTER_NAME_PARAM, StringUtils.EMPTY)
                .param(FILTER_MATCH_MODE_PARAM, MatchMode.RANGE.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifiersOptionsPageWithNullMatchMode() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE))
                .param(FILTER_NAME_PARAM, ClassifierOptionsDatabaseModel_.CREATION_DATE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifiersOptionsPageOk() throws Exception {
        Page<ClassifierOptionsDatabaseModel> page = Mockito.mock(Page.class);
        when(page.getTotalElements()).thenReturn(TOTAL_ELEMENTS);
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels = Collections.singletonList(
                TestHelperUtils.createClassifierOptionsDatabaseModel(OPTIONS, classifiersConfiguration));
        PageDto<ClassifierOptionsDto> pageDto =
                PageDto.of(classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModels), PAGE_NUMBER,
                        TOTAL_ELEMENTS);
        when(page.getContent()).thenReturn(classifierOptionsDatabaseModels);
        when(classifierOptionsService.getNextPage(anyLong(), any(PageRequestDto.class))).thenReturn(page);
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(pageDto)));
    }

    @Test
    public void testDeleteOptionsUnauthorized() throws Exception {
        mockMvc.perform(delete(DELETE_URL)
                .param(ID_PARAM, String.valueOf(CONFIGURATION_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteOptionsWithNotSpecifiedConfigurationId() throws Exception {
        mockMvc.perform(delete(DELETE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteNotExistingOptions() throws Exception {
        doThrow(new EntityNotFoundException()).when(classifierOptionsService).deleteOptions(CONFIGURATION_ID);
        mockMvc.perform(delete(DELETE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(CONFIGURATION_ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteOptionsForBuildInConfiguration() throws Exception {
        doThrow(new IllegalStateException()).when(classifierOptionsService).deleteOptions(CONFIGURATION_ID);
        mockMvc.perform(delete(DELETE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(CONFIGURATION_ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteOptionsOk() throws Exception {
        mockMvc.perform(delete(DELETE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(CONFIGURATION_ID)))
                .andExpect(status().isOk());
    }

    @Test
    public void testSaveOptionsUnauthorized() throws Exception {
        mockMvc.perform(multipart(SAVE_URL)
                .file(createClassifierOptionsFileMock())
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testSaveOptionsWithNotSpecifiedConfigurationId() throws Exception {
        mockMvc.perform(multipart(SAVE_URL)
                .file(createClassifierOptionsFileMock())
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSaveOptionsWithNotSpecifiedFile() throws Exception {
        mockMvc.perform(multipart(SAVE_URL)
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSaveOptionsOk() throws Exception {
        mockMvc.perform(multipart(SAVE_URL)
                .file(createClassifierOptionsFileMock())
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk());
    }

    @Test
    public void testSaveInvalidOptions() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile(CLASSIFIER_OPTIONS_FILE_PARAM, "test.json",
                MimeTypeUtils.APPLICATION_JSON.toString(), "content".getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(multipart(SAVE_URL)
                .file(multipartFile)
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    private MockMultipartFile createClassifierOptionsFileMock() throws JsonProcessingException {
        LogisticOptions logisticOptions = TestHelperUtils.createLogisticOptions();
        String content = objectMapper.writeValueAsString(logisticOptions);
        return new MockMultipartFile(CLASSIFIER_OPTIONS_FILE_PARAM,
                String.format("%s.json", logisticOptions.getClass().getSimpleName()),
                MimeTypeUtils.APPLICATION_JSON.toString(), content.getBytes(StandardCharsets.UTF_8));
    }
}
