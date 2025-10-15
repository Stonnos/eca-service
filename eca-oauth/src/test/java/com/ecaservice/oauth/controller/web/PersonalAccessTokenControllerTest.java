package com.ecaservice.oauth.controller.web;

import com.ecaservice.oauth.service.PersonalAccessTokenService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PersonalAccessTokenDto;
import com.ecaservice.web.dto.model.SimplePageRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

import static com.ecaservice.oauth.TestHelperUtils.createPageRequestDto;
import static com.ecaservice.oauth.TestHelperUtils.createPersonalAccessToken;
import static com.ecaservice.oauth.TestHelperUtils.createPersonalAccessTokenDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link PersonalAccessTokenController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = PersonalAccessTokenController.class)
class PersonalAccessTokenControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/personal-access-token";
    private static final String CREATE_TOKEN_URL = BASE_URL + "/create";
    private static final String REMOVE_TOKEN_URL = BASE_URL + "/remove/{id}";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final long ID = 1L;
    private static final long TOTAL_ELEMENTS = 1L;
    private static final int PAGE_NUMBER = 0;

    @MockBean
    private PersonalAccessTokenService personalAccessTokenService;

    @Test
    void testCreateTokenUnauthorized() throws Exception {
        var personalAccessTokenDto = createPersonalAccessTokenDto();
        mockMvc.perform(post(CREATE_TOKEN_URL)
                        .content(objectMapper.writeValueAsString(personalAccessTokenDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateTokenOk() throws Exception {
        var personalAccessTokenDto = createPersonalAccessTokenDto();
        mockMvc.perform(post(CREATE_TOKEN_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .content(objectMapper.writeValueAsString(personalAccessTokenDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testRemoveTokenUnauthorized() throws Exception {
        mockMvc.perform(delete(REMOVE_TOKEN_URL, ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRemoveTokenOk() throws Exception {
        mockMvc.perform(delete(REMOVE_TOKEN_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testTokensPageUnauthorized() throws Exception {
        mockMvc.perform(post(LIST_URL)
                        .content(objectMapper.writeValueAsString(createPageRequestDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTokensPage() throws Exception {
        var tokens = Collections.singletonList(createPersonalAccessToken());
        PageDto<PersonalAccessTokenDto> expected = PageDto.of(tokens, PAGE_NUMBER, TOTAL_ELEMENTS);
        when(personalAccessTokenService.getNextPage(any(SimplePageRequestDto.class), anyString())).thenReturn(expected);
        mockMvc.perform(post(LIST_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .content(objectMapper.writeValueAsString(createPageRequestDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }
}
