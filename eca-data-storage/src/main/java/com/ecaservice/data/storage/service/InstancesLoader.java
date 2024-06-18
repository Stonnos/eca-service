package com.ecaservice.data.storage.service;

import com.ecaservice.common.web.exception.FileProcessingException;
import com.ecaservice.common.web.exception.InvalidFileException;
import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.model.MultipartFileResource;
import eca.data.file.FileDataLoader;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import static com.ecaservice.common.web.util.FileUtils.isValidExtension;

/**
 * Service to load instances.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesLoader {

    private final FileDataLoader fileDataLoader;
    private final EcaDsConfig ecaDsConfig;

    /**
     * Loads instances from multipart resource.
     *
     * @param multipartFileResource - multipart resource
     * @return instances object
     */
    @NewSpan
    public Instances load(MultipartFileResource multipartFileResource) {
        if (!isValidExtension(multipartFileResource.getFile(), ecaDsConfig.getSupportedDataFileExtensions())) {
            throw new InvalidFileException(
                    String.format("Invalid file [%s] extension. Expected one of %s", multipartFileResource.getFile(),
                            ecaDsConfig.getSupportedDataFileExtensions()));
        }
        log.info("Starting to load data from file {}", multipartFileResource.getFile());
        fileDataLoader.setSource(multipartFileResource);
        try {
            Instances data = fileDataLoader.loadInstances();
            log.info("{} data has been successfully loaded from file {}", data.relationName(),
                    multipartFileResource.getFile());
            return data;
        } catch (Exception ex) {
            log.error("There was an error while load data from file {}: {}", multipartFileResource.getFile(),
                    ex.getMessage());
            throw new FileProcessingException(ex.getMessage());
        }
    }
}
