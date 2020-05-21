package com.ecaservice.controller.web;

import com.ecaservice.exception.EntityNotFoundException;
import com.ecaservice.mapping.ClassifiersConfigurationMapperImpl;
import com.ecaservice.service.classifiers.ClassifiersConfigurationService;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import com.ecaservice.web.dto.util.FieldConstraints;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

import static com.ecaservice.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.PageRequestUtils.PAGE_NUMBER_PARAM;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE_PARAM;
import static com.ecaservice.PageRequestUtils.TOTAL_ELEMENTS;
import static com.ecaservice.TestHelperUtils.bearerHeader;
import static com.ecaservice.TestHelperUtils.createClassifiersConfigurationDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ClassifiersConfigurationController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = ClassifiersConfigurationController.class)
@Import(ClassifiersConfigurationMapperImpl.class)
public class ClassifiersConfigurationControllerTest extends PageRequestControllerTest {

    private static final String BASE_URL = "/experiment/classifiers-configurations";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String DETAIL_URL = BASE_URL + "/details/{id}";
    private static final String DELETE_URL = BASE_URL + "/delete";
    private static final String SET_ACTIVE_URL = BASE_URL + "/set-active";
    private static final String SAVE_URL = BASE_URL + "/save";
    private static final String UPDATE_URL = BASE_URL + "/update";

    private static final String ID_PARAM = "id";
    private static final long ID = 1L;
    static final String CONFIGURATION_NAME = "ConfigurationName";

    @MockBean
    private ClassifiersConfigurationService classifiersConfigurationService;

    @Test
    void testGetClassifiersConfigurationsPageUnauthorized() throws Exception {
        testGetPageUnauthorized(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifiersConfigurationsPageWithNullPageNumber() throws Exception {
        testGetPageWithNullPageNumber(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifiersConfigurationsPageWithNullPageSize() throws Exception {
        testGetPageWithNullPageSize(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifiersConfigurationsPageWithZeroPageSize() throws Exception {
        testGetPageWithZeroPageSize(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifiersConfigurationsPageWithNegativePageNumber() throws Exception {
        testGetPageWithNegativePageNumber(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifiersConfigurationsPageWithEmptyFilterRequestName() throws Exception {
        testGetPageWithEmptyFilterRequestName(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifiersConfigurationsPageWithNullMatchMode() throws Exception {
        testGetPageWithNullMatchMode(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifiersConfigurationsPageOk() throws Exception {
        PageDto<ClassifiersConfigurationDto> pageDto =
                PageDto.of(Collections.singletonList(createClassifiersConfigurationDto()), PAGE_NUMBER, TOTAL_ELEMENTS);
        when(classifiersConfigurationService.getClassifiersConfigurations(any(PageRequestDto.class))).thenReturn(
                pageDto);
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(pageDto)));
    }

    @Test
    void testGetClassifiersConfigurationDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(DETAIL_URL, ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetClassifiersConfigurationDetailsNotFound() throws Exception {
        doThrow(new EntityNotFoundException()).when(classifiersConfigurationService).getClassifiersConfigurationDetails(
                ID);
        mockMvc.perform(get(DETAIL_URL, ID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetClassifiersConfigurationDetailsOk() throws Exception {
        ClassifiersConfigurationDto classifiersConfigurationDto = createClassifiersConfigurationDto();
        when(classifiersConfigurationService.getClassifiersConfigurationDetails(ID)).thenReturn(
                classifiersConfigurationDto);
        mockMvc.perform(get(DETAIL_URL, ID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(classifiersConfigurationDto)));
    }

    @Test
    void testDeleteClassifiersConfigurationUnauthorized() throws Exception {
        mockMvc.perform(delete(DELETE_URL)
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteClassifiersConfigurationWithNullId() throws Exception {
        mockMvc.perform(delete(DELETE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteClassifiersConfigurationOk() throws Exception {
        mockMvc.perform(delete(DELETE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteConfigurationWithIllegalStateException() throws Exception {
        doThrow(new IllegalStateException()).when(classifiersConfigurationService).delete(ID);
        mockMvc.perform(delete(DELETE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSetActiveClassifiersConfigurationUnauthorized() throws Exception {
        mockMvc.perform(post(SET_ACTIVE_URL)
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSetActiveClassifiersConfigurationWithNullId() throws Exception {
        mockMvc.perform(post(SET_ACTIVE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSetActiveClassifiersConfigurationOk() throws Exception {
        mockMvc.perform(post(SET_ACTIVE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isOk());
    }

    @Test
    void testSetActiveNotExistingClassifiersConfiguration() throws Exception {
        doThrow(new EntityNotFoundException()).when(classifiersConfigurationService).setActive(ID);
        mockMvc.perform(post(SET_ACTIVE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSetActiveClassifiersConfigurationWithIllegalStateException() throws Exception {
        doThrow(new IllegalStateException()).when(classifiersConfigurationService).setActive(ID);
        mockMvc.perform(post(SET_ACTIVE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveClassifiersConfigurationUnauthorized() throws Exception {
        CreateClassifiersConfigurationDto createClassifiersConfigurationDto =
                new CreateClassifiersConfigurationDto(CONFIGURATION_NAME);
        mockMvc.perform(post(SAVE_URL)
                .content(objectMapper.writeValueAsString(createClassifiersConfigurationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSaveEmptyClassifiersConfiguration() throws Exception {
        mockMvc.perform(post(SAVE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveClassifiersConfigurationWithEmptyName() throws Exception {
        CreateClassifiersConfigurationDto createClassifiersConfigurationDto =
                new CreateClassifiersConfigurationDto(StringUtils.EMPTY);
        mockMvc.perform(post(SAVE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(createClassifiersConfigurationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveClassifiersConfigurationWithLargeName() throws Exception {
        CreateClassifiersConfigurationDto createClassifiersConfigurationDto = new CreateClassifiersConfigurationDto(
                StringUtils.repeat('Q', FieldConstraints.CONFIGURATION_NAME_MAX_LENGTH + 1));
        mockMvc.perform(post(SAVE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(createClassifiersConfigurationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveClassifiersConfigurationOk() throws Exception {
        CreateClassifiersConfigurationDto createClassifiersConfigurationDto =
                new CreateClassifiersConfigurationDto(CONFIGURATION_NAME);
        mockMvc.perform(post(SAVE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(createClassifiersConfigurationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateClassifiersConfigurationUnauthorized() throws Exception {
        UpdateClassifiersConfigurationDto updateClassifiersConfigurationDto =
                new UpdateClassifiersConfigurationDto(ID, CONFIGURATION_NAME);
        mockMvc.perform(put(UPDATE_URL)
                .content(objectMapper.writeValueAsString(updateClassifiersConfigurationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateEmptyClassifiersConfiguration() throws Exception {
        mockMvc.perform(put(UPDATE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateClassifiersConfigurationWithEmptyName() throws Exception {
        UpdateClassifiersConfigurationDto updateClassifiersConfigurationDto =
                new UpdateClassifiersConfigurationDto(ID, StringUtils.EMPTY);
        mockMvc.perform(put(UPDATE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(updateClassifiersConfigurationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateClassifiersConfigurationWithLargeName() throws Exception {
        UpdateClassifiersConfigurationDto updateClassifiersConfigurationDto = new UpdateClassifiersConfigurationDto(ID,
                StringUtils.repeat('Q', FieldConstraints.CONFIGURATION_NAME_MAX_LENGTH + 1));
        mockMvc.perform(put(UPDATE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(updateClassifiersConfigurationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateClassifiersConfigurationOk() throws Exception {
        UpdateClassifiersConfigurationDto updateClassifiersConfigurationDto =
                new UpdateClassifiersConfigurationDto(ID, CONFIGURATION_NAME);
        mockMvc.perform(put(UPDATE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(updateClassifiersConfigurationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
