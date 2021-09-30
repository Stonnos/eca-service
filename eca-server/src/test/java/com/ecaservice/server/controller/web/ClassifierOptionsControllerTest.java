package com.ecaservice.server.controller.web;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.classifier.options.model.LogisticOptions;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.server.mapping.ClassifierOptionsDatabaseModelMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.service.classifiers.ClassifierOptionsService;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ecaservice.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.PageRequestUtils.TOTAL_ELEMENTS;
import static com.ecaservice.TestHelperUtils.bearerHeader;
import static com.ecaservice.TestHelperUtils.createClassifiersConfiguration;
import static com.ecaservice.TestHelperUtils.createPageRequestDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ClassifierOptionsController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = ClassifierOptionsController.class)
@Import({ClassifierOptionsDatabaseModelMapperImpl.class, DateTimeConverter.class})
class ClassifierOptionsControllerTest extends PageRequestControllerTest {

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
    void testGetActiveOptionsUnauthorized() throws Exception {
        mockMvc.perform(get(ACTIVE_OPTIONS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetActiveOptionsOk() throws Exception {
        ClassifiersConfiguration classifiersConfiguration = createClassifiersConfiguration();
        List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels = Collections.singletonList(
                TestHelperUtils.createClassifierOptionsDatabaseModel(OPTIONS, classifiersConfiguration));
        List<ClassifierOptionsDto> classifierOptionsDtoList =
                classifierOptionsDatabaseModelMapper.map(classifierOptionsDatabaseModels);
        when(classifierOptionsService.getActiveClassifiersOptions()).thenReturn(classifierOptionsDatabaseModels);
        mockMvc.perform(get(ACTIVE_OPTIONS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(classifierOptionsDtoList)));
    }

    @Test
    void testGetClassifiersOptionsPageUnauthorized() throws Exception {
        testGetPageUnauthorized(PAGE_URL, buildParamsMap());
    }

    @Test
    void testGetClassifiersOptionsPageWithNullConfigurationId() throws Exception {
        mockMvc.perform(post(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetClassifiersOptionsPageWithNullPageNumber() throws Exception {
        testGetPageWithNullPageNumber(PAGE_URL, buildParamsMap());
    }

    @Test
    void testGetClassifiersOptionsPageWithNullPageSize() throws Exception {
        testGetPageWithNullPageSize(PAGE_URL, buildParamsMap());
    }

    @Test
    void testGetClassifiersOptionsPageWithZeroPageSize() throws Exception {
        testGetPageWithZeroPageSize(PAGE_URL, buildParamsMap());
    }

    @Test
    void testGetClassifiersOptionsPageWithNegativePageNumber() throws Exception {
        testGetPageWithNegativePageNumber(PAGE_URL, buildParamsMap());
    }

    @Test
    void testGetClassifiersOptionsPageWithEmptyFilterRequestName() throws Exception {
        testGetPageWithEmptyFilterRequestName(PAGE_URL, buildParamsMap());
    }

    @Test
    void testGetClassifiersOptionsPageWithNullMatchMode() throws Exception {
        testGetPageWithNullMatchMode(PAGE_URL, buildParamsMap());
    }

    @Test
    void testGetClassifiersOptionsPageOk() throws Exception {
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
        mockMvc.perform(post(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(pageDto)));
    }

    @Test
    void testDeleteOptionsUnauthorized() throws Exception {
        mockMvc.perform(delete(DELETE_URL)
                .param(ID_PARAM, String.valueOf(CONFIGURATION_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteOptionsWithNotSpecifiedConfigurationId() throws Exception {
        mockMvc.perform(delete(DELETE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteNotExistingOptions() throws Exception {
        doThrow(EntityNotFoundException.class).when(classifierOptionsService).deleteOptions(CONFIGURATION_ID);
        mockMvc.perform(delete(DELETE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(CONFIGURATION_ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteOptionsForBuildInConfiguration() throws Exception {
        doThrow(new IllegalStateException()).when(classifierOptionsService).deleteOptions(CONFIGURATION_ID);
        mockMvc.perform(delete(DELETE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(CONFIGURATION_ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteOptionsOk() throws Exception {
        mockMvc.perform(delete(DELETE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(CONFIGURATION_ID)))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveOptionsUnauthorized() throws Exception {
        mockMvc.perform(multipart(SAVE_URL)
                .file(createClassifierOptionsFileMock())
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSaveOptionsWithNotSpecifiedConfigurationId() throws Exception {
        mockMvc.perform(multipart(SAVE_URL)
                .file(createClassifierOptionsFileMock())
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveOptionsWithNotSpecifiedFile() throws Exception {
        mockMvc.perform(multipart(SAVE_URL)
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveOptionsOk() throws Exception {
        mockMvc.perform(multipart(SAVE_URL)
                .file(createClassifierOptionsFileMock())
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testSaveInvalidOptions() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile(CLASSIFIER_OPTIONS_FILE_PARAM, "test.json",
                MimeTypeUtils.APPLICATION_JSON.toString(), "content".getBytes(StandardCharsets.UTF_8));
        mockMvc.perform(multipart(SAVE_URL)
                .file(multipartFile)
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private MockMultipartFile createClassifierOptionsFileMock() throws JsonProcessingException {
        LogisticOptions logisticOptions = TestHelperUtils.createLogisticOptions();
        String content = objectMapper.writeValueAsString(logisticOptions);
        return new MockMultipartFile(CLASSIFIER_OPTIONS_FILE_PARAM,
                String.format("%s.json", logisticOptions.getClass().getSimpleName()),
                MimeTypeUtils.APPLICATION_JSON.toString(), content.getBytes(StandardCharsets.UTF_8));
    }

    private Map<String, List<String>> buildParamsMap() {
        return Collections.singletonMap(CONFIGURATION_ID_PARAM,
                Collections.singletonList(String.valueOf(CONFIGURATION_ID)));
    }
}
