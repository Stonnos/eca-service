package com.ecaservice.server.controller.web;

import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.web.dto.model.AttributeValueMetaInfoDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.S3ContentResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.TOTAL_ELEMENTS;
import static com.ecaservice.server.TestHelperUtils.createInstancesInfoDetailsDto;
import static com.ecaservice.server.TestHelperUtils.createPageRequestDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link InstancesInfoController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = InstancesInfoController.class)
class InstancesInfoControllerTest extends PageRequestControllerTest {

    private static final String BASE_URL = "/instances-info";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String DETAILS_URL = BASE_URL + "/details/{instancesId}";
    private static final String DOWNLOAD_URL = BASE_URL + "/download/{instancesId}";
    private static final String CLASS_VALUES_URL = BASE_URL + "/class-values/{instancesId}";
    private static final long INSTANCES_ID = 2;
    private static final String CONTENT_URL = "http://localhost:9000/content";

    @MockBean
    private InstancesInfoService instancesInfoService;

    @Test
    void testGetInstancesInfoPageUnauthorized() throws Exception {
        testGetPageUnauthorized(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetInstancesInfoPageWithNullPageNumber() throws Exception {
        testGetPageWithNullPageNumber(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetInstancesInfoPageWithNullPageSize() throws Exception {
        testGetPageWithNullPageSize(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetInstancesInfoPageWithZeroPageSize() throws Exception {
        testGetPageWithZeroPageSize(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetInstancesInfoPageWithNegativePageNumber() throws Exception {
        testGetPageWithNegativePageNumber(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetInstancesInfoPageOk() throws Exception {
        var instancesInfoPage = Collections.singletonList(TestHelperUtils.createInstancesInfoDto());
        var pageDto = PageDto.of(instancesInfoPage, PAGE_NUMBER, TOTAL_ELEMENTS);
        when(instancesInfoService.getNextPage(any(PageRequestDto.class))).thenReturn(pageDto);
        mockMvc.perform(post(LIST_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .content(objectMapper.writeValueAsString(createPageRequestDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(pageDto)));
    }

    @Test
    void testGetClassValuesUnauthorized() throws Exception {
        mockMvc.perform(get(CLASS_VALUES_URL, INSTANCES_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetClassValuesOk() throws Exception {
        var classValues = List.of(
                new AttributeValueMetaInfoDto(0, "val1"),
                new AttributeValueMetaInfoDto(1, "val2")
        );
        when(instancesInfoService.getClassValues(INSTANCES_ID)).thenReturn(classValues);
        mockMvc.perform(get(CLASS_VALUES_URL, INSTANCES_ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(classValues)));
    }

    @Test
    void testGetInstancesInfoDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(DETAILS_URL, INSTANCES_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetInstancesInfoDetailsOk() throws Exception {
        var instancesInfoDetailsDto = createInstancesInfoDetailsDto();
        when(instancesInfoService.getInstancesInfoDetails(INSTANCES_ID)).thenReturn(instancesInfoDetailsDto);
        mockMvc.perform(get(DETAILS_URL, INSTANCES_ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(instancesInfoDetailsDto)));
    }

    @Test
    void testGetInstancesDownloadUrlUnauthorized() throws Exception {
        mockMvc.perform(get(DOWNLOAD_URL, INSTANCES_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetDownloadUrlOk() throws Exception {
        var s3ContentResponseDto = S3ContentResponseDto.builder()
                .contentUrl(CONTENT_URL)
                .build();
        when(instancesInfoService.getDownloadUrl(INSTANCES_ID)).thenReturn(s3ContentResponseDto);
        mockMvc.perform(get(DOWNLOAD_URL, INSTANCES_ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(s3ContentResponseDto)));
    }
}
