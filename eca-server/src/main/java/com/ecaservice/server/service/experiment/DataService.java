package com.ecaservice.server.service.experiment;

import com.ecaservice.common.web.exception.FileProcessingException;
import com.ecaservice.common.web.exception.InvalidFileException;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.DataResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import static eca.data.FileUtils.ALL_EXTENSIONS;
import static eca.data.FileUtils.isValidTrainDataFile;

/**
 * Data service interface.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {

    /**
     * Loads data from file.
     *
     * @param dataResource - data resource
     * @return training data
     */
    public Instances load(DataResource<?> dataResource) {
        if (!isValidTrainDataFile(dataResource.getFile())) {
            throw new InvalidFileException(
                    String.format("Invalid file [%s] extension. Expected one of %s", dataResource.getFile(),
                            ALL_EXTENSIONS));
        }
        log.info("Starting to load data from file {}", dataResource.getFile());
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setSource(dataResource);
        try {
            Instances data = dataLoader.loadInstances();
            log.info("{} data has been successfully loaded from file {}", data.relationName(), dataResource.getFile());
            return data;
        } catch (Exception ex) {
            log.error("There was an error while load data from file {}: {}", dataResource.getFile(), ex.getMessage());
            throw new FileProcessingException(String.format("Error while process train data file [%s]",
                    dataResource.getFile()));
        }
    }
}
