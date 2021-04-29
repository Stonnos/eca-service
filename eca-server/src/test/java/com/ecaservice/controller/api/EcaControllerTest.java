package com.ecaservice.controller.api;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link EcaController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = EcaController.class)
@AutoConfigureMockMvc(addFilters = false)
class EcaControllerTest {

    private static final String BASE_URL = "/eca-api";
    private static final String DOWNLOAD_URL = BASE_URL + "/experiment/download/{token}";

    private static final String TOKEN = "token";

    @MockBean
    private ExperimentRepository experimentRepository;

    @Inject
    private MockMvc mockMvc;

    @Test
    void testExperimentNotExists() throws Exception {
        when(experimentRepository.findByToken(anyString())).thenThrow(new EntityNotFoundException());
        mockMvc.perform(get(DOWNLOAD_URL, TOKEN)).andExpect(status().isBadRequest());
    }

    @Test
    void testResultsFileNotExists() throws Exception {
        when(experimentRepository.findByToken(anyString())).thenReturn(Optional.of(new Experiment()));
        mockMvc.perform(get(DOWNLOAD_URL, TOKEN)).andExpect(status().isBadRequest());
    }
}
