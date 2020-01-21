package com.ecaservice.controller.api;

import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.util.ExperimentUtils;
import com.ecaservice.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.io.File;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link EcaController} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({Utils.class, ExperimentUtils.class})
@WebMvcTest(controllers = EcaController.class, secure = false)
public class EcaControllerTest {

    private static final String BASE_URL = "/eca-api";
    private static final String DOWNLOAD_URL = BASE_URL + "/experiment/download/{token}";

    private static final String TOKEN = "token";
    private static final String MODEL_FILE = "experiment.model";

    @MockBean
    private ExperimentRepository experimentRepository;

    @Inject
    private MockMvc mockMvc;

    @Test
    public void testExperimentNotExists() throws Exception {
        when(experimentRepository.findByToken(anyString())).thenReturn(null);
        mockMvc.perform(get(DOWNLOAD_URL, TOKEN)).andExpect(status().isBadRequest());
    }

    @Test
    public void testResultsFileNotExists() throws Exception {
        PowerMockito.mockStatic(ExperimentUtils.class);
        PowerMockito.mockStatic(Utils.class);
        when(experimentRepository.findByToken(anyString())).thenReturn(new Experiment());
        when(ExperimentUtils.getExperimentFile(any(Experiment.class), any())).thenReturn(new File(MODEL_FILE));
        when(Utils.existsFile(any(File.class))).thenReturn(false);
        mockMvc.perform(get(DOWNLOAD_URL, TOKEN)).andExpect(status().isBadRequest());
    }
}
