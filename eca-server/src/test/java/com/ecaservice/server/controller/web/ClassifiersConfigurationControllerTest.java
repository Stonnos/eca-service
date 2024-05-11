package com.ecaservice.server.controller.web;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.mapping.ClassifiersConfigurationMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.report.model.ClassifiersConfigurationBean;
import com.ecaservice.server.service.UserService;
import com.ecaservice.server.service.classifiers.ClassifiersConfigurationHistoryService;
import com.ecaservice.server.service.classifiers.ClassifiersConfigurationService;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import com.ecaservice.web.dto.util.FieldConstraints;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.TOTAL_ELEMENTS;
import static com.ecaservice.server.TestHelperUtils.bearerHeader;
import static com.ecaservice.server.TestHelperUtils.createClassifiersConfigurationDto;
import static com.ecaservice.server.TestHelperUtils.createClassifiersConfigurationHistoryDto;
import static com.ecaservice.server.TestHelperUtils.createPageRequestDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
@Disabled
@WebMvcTest(controllers = ClassifiersConfigurationController.class)
@Import({ClassifiersConfigurationMapperImpl.class, DateTimeConverter.class})
class ClassifiersConfigurationControllerTest extends PageRequestControllerTest {

    private static final String BASE_URL = "/experiment/classifiers-configurations";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String HISTORY_URL = BASE_URL + "/history";
    private static final String DETAIL_URL = BASE_URL + "/details/{id}";
    private static final String DELETE_URL = BASE_URL + "/delete";
    private static final String SET_ACTIVE_URL = BASE_URL + "/set-active";
    private static final String SAVE_URL = BASE_URL + "/save";
    private static final String COPY_URL = BASE_URL + "/copy";
    private static final String UPDATE_URL = BASE_URL + "/update";
    private static final String REPORT_URL = BASE_URL + "/report/{id}";

    private static final String CONFIGURATION_NAME = "ConfigurationName";
    private static final String ID_PARAM = "id";
    private static final long ID = 1L;

    private static final String CONFIGURATION_ID_PARAM = "configurationId";
    private static final long CONFIGURATION_ID = 1L;

    @MockBean
    private ClassifiersConfigurationService classifiersConfigurationService;
    @MockBean
    private ClassifiersConfigurationHistoryService classifiersConfigurationHistoryService;
    @MockBean
    private UserService userService;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;

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
        mockMvc.perform(post(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(pageDto)));
    }

    @Test
    void testGetClassifiersConfigurationHistoryPageUnauthorized() throws Exception {
        testGetPageUnauthorized(HISTORY_URL, buildConfigurationIdParam());
    }

    @Test
    void testGetClassifiersConfigurationHistoryPageWithNullPageNumber() throws Exception {
        testGetPageWithNullPageNumber(HISTORY_URL, buildConfigurationIdParam());
    }

    @Test
    void testGetClassifiersConfigurationHistoryPageWithNullPageSize() throws Exception {
        testGetPageWithNullPageSize(HISTORY_URL, buildConfigurationIdParam());
    }

    @Test
    void testGetClassifiersConfigurationHistoryPageWithZeroPageSize() throws Exception {
        testGetPageWithZeroPageSize(HISTORY_URL, buildConfigurationIdParam());
    }

    @Test
    void testGetClassifiersConfigurationHistoryPageWithNegativePageNumber() throws Exception {
        testGetPageWithNegativePageNumber(HISTORY_URL, buildConfigurationIdParam());
    }

    @Test
    void testGetClassifiersConfigurationHistoryPageWithEmptyFilterRequestName() throws Exception {
        testGetPageWithEmptyFilterRequestName(HISTORY_URL, buildConfigurationIdParam());
    }

    @Test
    void testGetClassifiersConfigurationHistoryPageWithNullMatchMode() throws Exception {
        testGetPageWithNullMatchMode(HISTORY_URL, buildConfigurationIdParam());
    }

    @Test
    void testGetClassifiersConfigurationHistoryPageOk() throws Exception {
        var pageDto =
                PageDto.of(Collections.singletonList(createClassifiersConfigurationHistoryDto()), PAGE_NUMBER,
                        TOTAL_ELEMENTS);
        when(classifiersConfigurationHistoryService.getNextPage(anyLong(), any(PageRequestDto.class)))
                .thenReturn(pageDto);
        mockMvc.perform(post(HISTORY_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
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
        doThrow(EntityNotFoundException.class).when(classifiersConfigurationService)
                .getClassifiersConfigurationDetails(ID);
        mockMvc.perform(get(DETAIL_URL, ID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
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
        doThrow(EntityNotFoundException.class).when(classifiersConfigurationService).setActive(ID);
        mockMvc.perform(post(SET_ACTIVE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(ID_PARAM, String.valueOf(ID)))
                .andExpect(status().isBadRequest());
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

    @Test
    void testDownloadClassifiersConfigurationReportUnauthorized() throws Exception {
        mockMvc.perform(get(REPORT_URL, ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDownloadClassifiersConfigurationReportNotFound() throws Exception {
        doThrow(EntityNotFoundException.class).when(classifiersConfigurationService)
                .getClassifiersConfigurationReport(ID);
        mockMvc.perform(get(REPORT_URL, ID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDownloadClassifiersConfigurationReportOk() throws Exception {
        ClassifiersConfigurationBean classifiersConfigurationBean = new ClassifiersConfigurationBean();
        classifiersConfigurationBean.setClassifiersOptions(Collections.emptyList());
        when(classifiersConfigurationService.getClassifiersConfigurationReport(ID)).thenReturn(
                classifiersConfigurationBean);
        mockMvc.perform(get(REPORT_URL, ID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }

    @Test
    void testCopyClassifiersConfigurationUnauthorized() throws Exception {
        var configurationDto = new UpdateClassifiersConfigurationDto(ID, CONFIGURATION_NAME);
        mockMvc.perform(post(COPY_URL)
                .content(objectMapper.writeValueAsString(configurationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCopyClassifiersConfigurationWithEmptyName() throws Exception {
        var configurationDto = new UpdateClassifiersConfigurationDto(ID, StringUtils.EMPTY);
        mockMvc.perform(post(COPY_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(configurationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCopyClassifiersConfigurationWithLargeName() throws Exception {
        var configurationDto = new UpdateClassifiersConfigurationDto(ID,
                StringUtils.repeat('Q', FieldConstraints.CONFIGURATION_NAME_MAX_LENGTH + 1));
        mockMvc.perform(post(COPY_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(configurationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCopyClassifiersConfigurationOk() throws Exception {
        var configurationDto = new UpdateClassifiersConfigurationDto(ID, CONFIGURATION_NAME);
        mockMvc.perform(post(COPY_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(configurationDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private Map<String, List<String>> buildConfigurationIdParam() {
        return Collections.singletonMap(CONFIGURATION_ID_PARAM,
                Collections.singletonList(String.valueOf(CONFIGURATION_ID)));
    }
}
