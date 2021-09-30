package com.ecaservice.server.controller.web;

import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel_;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.dto.model.MatchMode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.PAGE_SIZE;
import static com.ecaservice.server.TestHelperUtils.bearerHeader;
import static com.ecaservice.server.TestHelperUtils.createPageRequestDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

abstract class PageRequestControllerTest extends AbstractControllerTest {

    public void testGetPageUnauthorized(String url, Map<String, List<String>> params) throws Exception {
        mockMvc.perform(post(url)
                .params(new LinkedMultiValueMap<>(params))
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    public void testGetPageWithNullPageNumber(String url, Map<String, List<String>> params) throws Exception {
        mockMvc.perform(post(url)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .params(new LinkedMultiValueMap<>(params))
                .content(objectMapper.writeValueAsString(createPageRequestDto(null, PAGE_SIZE)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    public void testGetPageWithNullPageSize(String url, Map<String, List<String>> params) throws Exception {
        mockMvc.perform(post(url)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .params(new LinkedMultiValueMap<>(params))
                .content(objectMapper.writeValueAsString(createPageRequestDto(PAGE_NUMBER, null)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    public void testGetPageWithZeroPageSize(String url, Map<String, List<String>> params) throws Exception {
        mockMvc.perform(post(url)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .params(new LinkedMultiValueMap<>(params))
                .content(objectMapper.writeValueAsString(createPageRequestDto(PAGE_NUMBER, 0)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    public void testGetPageWithNegativePageNumber(String url, Map<String, List<String>> params) throws Exception {
        mockMvc.perform(post(url)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .params(new LinkedMultiValueMap<>(params))
                .content(objectMapper.writeValueAsString(createPageRequestDto(PAGE_NUMBER, -1)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    public void testGetPageWithEmptyFilterRequestName(String url, Map<String, List<String>> params)
            throws Exception {
        var pageRequest = createPageRequestDto();
        pageRequest.getFilters()
                .add(new FilterRequestDto(StringUtils.EMPTY, Collections.emptyList(), MatchMode.RANGE));
        mockMvc.perform(post(url)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .params(new LinkedMultiValueMap<>(params))
                .content(objectMapper.writeValueAsString(pageRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    public void testGetPageWithNullMatchMode(String url, Map<String, List<String>> params) throws Exception {
        var pageRequest = createPageRequestDto();
        pageRequest.getFilters().add(
                new FilterRequestDto(ClassifierOptionsDatabaseModel_.CREATION_DATE, Collections.emptyList(), null));
        mockMvc.perform(post(url)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .params(new LinkedMultiValueMap<>(params))
                .content(objectMapper.writeValueAsString(pageRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
