package com.ecaservice.service.experiment;

import com.ecaservice.common.web.exception.FileProcessingException;
import com.ecaservice.common.web.exception.InvalidFileException;
import com.ecaservice.exception.experiment.ExperimentException;
import eca.core.ModelSerializationHelper;
import eca.data.DataFileExtension;
import eca.data.file.FileDataLoader;
import eca.data.file.FileDataSaver;
import eca.data.file.resource.DataResource;
import eca.data.file.resource.FileResource;
import eca.dataminer.AbstractExperiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ecaservice.util.Utils.isValidTrainDataFile;

/**
 * Data service interface.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataService {

    private static final List<String> TRAIN_DATA_FILE_EXTENSIONS = Stream.of(DataFileExtension.values())
            .map(DataFileExtension::getExtendedExtension)
            .collect(Collectors.toList());

    private final FileDataSaver dataSaver;

    /**
     * Saves data to file.
     *
     * @param file - file object
     * @param data - training data
     */
    public void save(File file, Instances data) {
        if (!isValidTrainDataFile(file.getName())) {
            throw new InvalidFileException(
                    String.format("Invalid file [%s] extension. Expected one of %s", file.getName(),
                            TRAIN_DATA_FILE_EXTENSIONS));
        }
        log.info("Starting to save {} data into file {}.", data.relationName(), file.getAbsolutePath());
        try {
            dataSaver.saveData(file, data);
            log.info("{} data has been successfully saved to file {}.", data.relationName(), file.getAbsolutePath());
        } catch (Exception ex) {
            log.error("There was an error while save data [{}] into file {}: {}", data.relationName(),
                    file.getAbsoluteFile(), ex.getMessage());
            throw new FileProcessingException(String.format("Error while process train data file [%s]", file.getName()));
        }
    }

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
                            TRAIN_DATA_FILE_EXTENSIONS));
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

    /**
     * Saves experiment history to file.
     *
     * @param file       - file object
     * @param experiment - experiment history
     * @throws Exception in case of I/O error
     */
    public void saveExperimentHistory(File file, AbstractExperiment<?> experiment) throws Exception {
        log.info("Starting to save experiment history to file {}", file.getAbsolutePath());
        ModelSerializationHelper.serialize(file, experiment);
        log.info("Experiment history has been successfully saved to file {}", file.getAbsolutePath());
    }

    /**
     * Loads experiment history from file.
     *
     * @param file - file object
     * @return experiment history
     * @throws Exception in case of I/O error
     */
    public AbstractExperiment<?> loadExperimentHistory(File file) throws Exception {
        log.info("Starting to load experiment history from file {}", file.getAbsolutePath());
        AbstractExperiment<?> experimentHistory =
                ModelSerializationHelper.deserialize(new FileResource(file), AbstractExperiment.class);
        log.info("Experiment history has been loaded from file {}", file.getAbsolutePath());
        return experimentHistory;
    }

    /**
     * Deletes file from disk.
     *
     * @param fileName - file name
     */
    public void delete(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            log.warn("Got empty file name. Mark as deleted");
        }
        File file = new File(fileName);
        if (!file.isFile()) {
            log.warn("File with name [{}] doesn't exists. Mark as deleted", file.getAbsolutePath());
        } else {
            try {
                FileUtils.forceDelete(file);
                log.info("File [{}] has been deleted from disk.", file.getAbsolutePath());
            } catch (IOException ex) {
                log.error("There was an error while deleting [{}] file from disk.", file.getAbsolutePath());
                throw new ExperimentException(ex.getMessage());
            }
        }
    }
}
