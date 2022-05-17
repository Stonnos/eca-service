package com.ecaservice.server.controller.web;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ErsEvaluationMethodMapperImpl;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.service.ers.ClassifierOptionsRequestService;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.TOTAL_ELEMENTS;
import static com.ecaservice.server.TestHelperUtils.bearerHeader;
import static com.ecaservice.server.TestHelperUtils.createPageRequestDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ClassifierOptionsRequestController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = ClassifierOptionsRequestController.class)
@Import({ClassifierOptionsRequestModelMapperImpl.class, ErsEvaluationMethodMapperImpl.class, DateTimeConverter.class})
class ClassifierOptionsRequestControllerTest extends PageRequestControllerTest {

    private static final String LIST_URL = "/classifiers-options-requests";

    private static final String DATA_MD5_HASH = "hash";

    @MockBean
    private ClassifierOptionsRequestService classifierOptionsRequestService;

    @Inject
    private ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;

    @Test
    void testGetClassifierOptionsRequestsUnauthorized() throws Exception {
        testGetPageUnauthorized(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifierOptionsRequestsWithNullPageNumber() throws Exception {
        testGetPageWithNullPageNumber(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifierOptionsRequestsWithNullPageSize() throws Exception {
        testGetPageWithNullPageSize(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifierOptionsRequestsWithZeroPageSize() throws Exception {
        testGetPageWithZeroPageSize(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifierOptionsRequestsWithNegativePageNumber() throws Exception {
        testGetPageWithNegativePageNumber(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifierOptionsRequestsWithEmptyFilterRequestName() throws Exception {
        testGetPageWithEmptyFilterRequestName(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifierOptionsRequestsPageWithNullMatchMode() throws Exception {
        testGetPageWithNullMatchMode(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetClassifierOptionsRequestsOk() throws Exception {
        var classifierOptionsRequestModels =
                Collections.singletonList(TestHelperUtils.createClassifierOptionsRequestModel(
                        DATA_MD5_HASH, LocalDateTime.now(), ErsResponseStatus.RESULTS_NOT_FOUND,
                        Collections.emptyList()));
        var classifierOptionsRequestDtoModel = classifierOptionsRequestModels.stream()
                .map(classifierOptionsRequestModelMapper::map)
                .collect(Collectors.toList());
        var pageDto = PageDto.of(classifierOptionsRequestDtoModel, PAGE_NUMBER, TOTAL_ELEMENTS);
        when(classifierOptionsRequestService.getClassifierOptionsRequestModels(any(PageRequestDto.class)))
                .thenReturn(pageDto);
        mockMvc.perform(post(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(pageDto)));
    }
}
