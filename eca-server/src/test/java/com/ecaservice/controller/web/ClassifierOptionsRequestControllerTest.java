package com.ecaservice.controller.web;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.configuation.annotation.Oauth2TestConfiguration;
import com.ecaservice.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.mapping.ClassifierOptionsRequestModelMapperImpl;
import com.ecaservice.mapping.ClassifierOptionsResponseModelMapperImpl;
import com.ecaservice.mapping.ErsEvaluationMethodMapperImpl;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel_;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.service.ers.ClassifierOptionsRequestService;
import com.ecaservice.token.TokenService;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
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
import java.time.LocalDateTime;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ClassifierOptionsRequestController} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ClassifierOptionsRequestController.class)
@Oauth2TestConfiguration
@Import({ClassifierOptionsRequestModelMapperImpl.class, ErsEvaluationMethodMapperImpl.class,
        ClassifierOptionsResponseModelMapperImpl.class})
public class ClassifierOptionsRequestControllerTest {

    private static final String LIST_URL = "/classifiers-options-requests";

    private static final String DATA_MD5_HASH = "hash";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private ClassifierOptionsRequestService classifierOptionsRequestService;

    @Inject
    private TokenService tokenService;
    @Inject
    private ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    @Inject
    private MockMvc mockMvc;

    private String accessToken;

    @Before
    public void init() throws Exception {
        accessToken = tokenService.obtainAccessToken();
    }

    @Test
    public void testGetClassifierOptionsRequestsUnauthorized() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetClassifierOptionsRequestsWithNullPageNumber() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifierOptionsRequestsWithNullPageSize() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifierOptionsRequestsWithEmptyFilterRequestName() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE))
                .param(FILTER_NAME_PARAM, StringUtils.EMPTY)
                .param(FILTER_MATCH_MODE_PARAM, MatchMode.RANGE.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifierOptionsRequestsPageWithNullMatchMode() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE))
                .param(FILTER_NAME_PARAM, ClassifierOptionsRequestModel_.REQUEST_DATE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetClassifierOptionsRequestsOk() throws Exception {
        Page<ClassifierOptionsRequestModel> page = Mockito.mock(Page.class);
        when(page.getTotalElements()).thenReturn(TOTAL_ELEMENTS);
        List<ClassifierOptionsRequestModel> classifierOptionsRequestModels =
                Collections.singletonList(TestHelperUtils.createClassifierOptionsRequestModel(
                        DATA_MD5_HASH, LocalDateTime.now(), ErsResponseStatus.RESULTS_NOT_FOUND,
                        Collections.emptyList()));
        PageDto<ClassifierOptionsRequestDto> pageDto =
                PageDto.of(classifierOptionsRequestModelMapper.map(classifierOptionsRequestModels), PAGE_NUMBER,
                        TOTAL_ELEMENTS);
        when(page.getContent()).thenReturn(classifierOptionsRequestModels);
        when(classifierOptionsRequestService.getNextPage(any(PageRequestDto.class))).thenReturn(page);
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(pageDto)));
    }
}
