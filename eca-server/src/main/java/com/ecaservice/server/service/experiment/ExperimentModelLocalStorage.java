package com.ecaservice.server.service.experiment;

import com.ecaservice.server.config.ExperimentConfig;
import eca.core.ModelSerializationHelper;
import eca.data.file.resource.FileResource;
import eca.dataminer.AbstractExperiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExperimentModelLocalStorage {

    private final ExperimentConfig experimentConfig;

    public void put(String requestId, AbstractExperiment<?> experiment) throws IOException {
        File file = getFile(requestId);
        ModelSerializationHelper.serialize(file, experiment);
    }

    public AbstractExperiment<?> get(String requestId) throws IOException {
        File file = getFile(requestId);
        FileResource fileResource = new FileResource(file);
        return ModelSerializationHelper.deserialize(fileResource, AbstractExperiment.class);
    }

    public void delete(String requestId) {
        File file = getFile(requestId);
        FileUtils.deleteQuietly(file);
    }

    private File getFile(String requestId) {
        return new File(String.format("%s/%s.model", experimentConfig.getExperimentLocalStoragePath(), requestId));
    }
}
