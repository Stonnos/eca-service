package com.ecaservice.data.storage.report;

import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.model.report.ReportProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Cleanup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final EcaDsConfig ecaDsConfig;

    @Getter
    private List<ReportProperties> reportProperties = newArrayList();

    /**
     * Initialize reports configuration.
     *
     * @throws IOException in case of I/O errors
     */
    @PostConstruct
    public void initializeReportsProperties() throws IOException {
        log.info("Starting to read reports config from [{}]", ecaDsConfig.getReportsPath());
        var resolver = new PathMatchingResourcePatternResolver();
        var resource = resolver.getResource(ecaDsConfig.getReportsPath());
        @Cleanup var inputStream = resource.getInputStream();
        reportProperties = OBJECT_MAPPER.readValue(inputStream, new TypeReference<>() {
        });
        log.info("Reports config has been read from [{}]", ecaDsConfig.getReportsPath());
    }
}
