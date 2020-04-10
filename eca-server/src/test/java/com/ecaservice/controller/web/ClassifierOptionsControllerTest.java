package com.ecaservice.controller.web;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.configuation.annotation.Oauth2TestConfiguration;
import com.ecaservice.mapping.ClassifierOptionsDatabaseModelMapper;
import com.ecaservice.mapping.ClassifierOptionsDatabaseModelMapperImpl;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel_;
import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.service.classifiers.ClassifierOptionsService;
import com.ecaservice.token.TokenService;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests fro checking {@link ClassifierOptionsController} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ClassifierOptionsController.class)
@Oauth2TestConfiguration
@Import(ClassifierOptionsDatabaseModelMapperImpl.class)
public class ClassifierOptionsControllerTest {

    private static final String BASE_URL = "/experiment/classifiers-options";
    private static final String ACTIVE_OPTIONS_URL = BASE_URL + "/active-options";
    private static final String PAGE_URL = BASE_URL + "/page";

    private static final String CONFIGURATION_ID_PARAM = "configurationId";
    private static final String OPTIONS = "options";
    private static final long CONFIGURATION_ID = 1L;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private ClassifierOptionsService classifierOptionsService;

    @Inject
    private TokenService tokenService;
    @Inject
    private ClassifierOptionsDatabaseModelMapper classifierOptionsDatabaseModelMapper;
    @Inject
    private MockMvc mockMvc;

    private String accessToken;

    @Before
    public void init() throws Exception {
        accessToken = tokenService.obtainAccessToken();
    }

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
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
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
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifiersOptionsPageWithNullPageNumber() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifiersOptionsPageWithNullPageSize() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifiersOptionsPageWithZeroPageSize() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifiersOptionsPageWithNegativePageNumber() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_NUMBER_PARAM, String.valueOf(-1))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifiersOptionsPageWithEmptyFilterRequestName() throws Exception {
        mockMvc.perform(get(PAGE_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
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
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
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
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(CONFIGURATION_ID_PARAM, String.valueOf(CONFIGURATION_ID))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(pageDto)));
    }
}
