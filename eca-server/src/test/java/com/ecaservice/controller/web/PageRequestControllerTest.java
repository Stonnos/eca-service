package com.ecaservice.controller.web;

import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel_;
import com.ecaservice.web.dto.model.MatchMode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.Map;

import static com.ecaservice.PageRequestUtils.FILTER_MATCH_MODE_PARAM;
import static com.ecaservice.PageRequestUtils.FILTER_NAME_PARAM;
import static com.ecaservice.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.PageRequestUtils.PAGE_NUMBER_PARAM;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE_PARAM;
import static com.ecaservice.TestHelperUtils.bearerHeader;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

abstract class PageRequestControllerTest extends AbstractControllerTest {

    public void testGetPageUnauthorized(String url, Map<String, List<String>> params) throws Exception {
        mockMvc.perform(get(url)
                .params(new LinkedMultiValueMap<>(params))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isUnauthorized());
    }

    public void testGetPageWithNullPageNumber(String url, Map<String, List<String>> params) throws Exception {
        mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .params(new LinkedMultiValueMap<>(params))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    public void testGetPageWithNullPageSize(String url, Map<String, List<String>> params) throws Exception {
        mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .params(new LinkedMultiValueMap<>(params))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER)))
                .andExpect(status().isBadRequest());
    }

    public void testGetPageWithZeroPageSize(String url, Map<String, List<String>> params) throws Exception {
        mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .params(new LinkedMultiValueMap<>(params))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(0)))
                .andExpect(status().isBadRequest());
    }

    public void testGetPageWithNegativePageNumber(String url, Map<String, List<String>> params) throws Exception {
        mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .params(new LinkedMultiValueMap<>(params))
                .param(PAGE_NUMBER_PARAM, String.valueOf(-1))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    public void testGetPageWithEmptyFilterRequestName(String url, Map<String, List<String>> params)
            throws Exception {
        mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .params(new LinkedMultiValueMap<>(params))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE))
                .param(FILTER_NAME_PARAM, StringUtils.EMPTY)
                .param(FILTER_MATCH_MODE_PARAM, MatchMode.RANGE.name()))
                .andExpect(status().isBadRequest());
    }

    public void testGetPageWithNullMatchMode(String url, Map<String, List<String>> params) throws Exception {
        mockMvc.perform(get(url)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .params(new LinkedMultiValueMap<>(params))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE))
                .param(FILTER_NAME_PARAM, ClassifierOptionsDatabaseModel_.CREATION_DATE))
                .andExpect(status().isBadRequest());
    }
}
