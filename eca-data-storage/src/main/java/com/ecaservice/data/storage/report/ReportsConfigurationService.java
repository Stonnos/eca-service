package com.ecaservice.data.storage.report;

import com.ecaservice.common.web.resource.JsonResourceLoader;
import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.model.report.ReportProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Report configuration service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportsConfigurationService {

    private final EcaDsConfig ecaDsConfig;

    private final JsonResourceLoader jsonResourceLoader = new JsonResourceLoader();

    @Getter
    private List<ReportProperties> reportProperties = newArrayList();

    /**
     * Initialize reports configuration.
     *
     * @throws IOException in case of I/O errors
     */
    @PostConstruct
    public void initializeReportsProperties() throws IOException {
        reportProperties = jsonResourceLoader.load(ecaDsConfig.getReportsPath(), new TypeReference<>() {
        });
    }
}
