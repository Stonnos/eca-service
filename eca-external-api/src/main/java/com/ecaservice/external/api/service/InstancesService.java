package com.ecaservice.external.api.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.exception.DataNotFoundException;
import com.ecaservice.external.api.repository.InstancesRepository;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.DataResource;
import eca.data.file.resource.FileResource;
import eca.data.file.resource.UrlResource;
import io.micrometer.core.annotation.Timed;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.external.api.config.metrics.MetricConstants.LOAD_INSTANCES_METRIC;
import static com.ecaservice.external.api.config.metrics.MetricConstants.UPLOAD_INSTANCES_METRIC;
import static com.ecaservice.external.api.util.Constants.DATA_URL_PREFIX;

/**
 * Instances service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class InstancesService {

    private static final String FILE_NAME_FORMAT = "%s_%s.%s";

    private final ExternalApiConfig externalApiConfig;
    private final FileDataLoader fileDataLoader;
    private final FileDataService fileDataService;
    private final InstancesRepository instancesRepository;

    /**
     * Upload train data file to file system.
     *
     * @param trainingData - train data file
     * @return instance entity
     * @throws IOException in case of error
     */
    @Timed(value = UPLOAD_INSTANCES_METRIC)
    public InstancesEntity uploadInstances(MultipartFile trainingData) throws IOException {
        log.debug("Starting to upload train data [{}] to file system", trainingData.getOriginalFilename());
        String dataUuid = UUID.randomUUID().toString();
        File file = copyToFile(trainingData, dataUuid);
        InstancesEntity instancesEntity = new InstancesEntity();
        instancesEntity.setAbsolutePath(file.getAbsolutePath());
        instancesEntity.setUuid(dataUuid);
        instancesEntity.setCreationDate(LocalDateTime.now());
        instancesRepository.save(instancesEntity);
        log.debug("Train data [{}] has been uploaded to file system with uuid [{}]",
                trainingData.getOriginalFilename(), dataUuid);
        return instancesEntity;
    }

    /**
     * Loads instances for specified url.
     *
     * @param url - url string
     * @return instances object
     */
    @Timed(value = LOAD_INSTANCES_METRIC)
    public Instances loadInstances(String url) {
        try {
            DataResource<?> dataResource = createDataResource(url);
            fileDataLoader.setSource(dataResource);
            return fileDataLoader.loadInstances();
        } catch (Exception ex) {
            throw new DataNotFoundException(ex.getMessage());
        }
    }

    private File copyToFile(MultipartFile multipartFile, String dataUuid) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileName);
        String baseName = FilenameUtils.getBaseName(fileName);
        String trainDataFinalName = String.format(FILE_NAME_FORMAT, baseName, dataUuid, extension);
        File destination = new File(externalApiConfig.getTrainDataPath(), trainDataFinalName);
        @Cleanup InputStream inputStream = multipartFile.getInputStream();
        fileDataService.copyToFile(inputStream, destination);
        return destination;
    }

    private DataResource<?> createDataResource(String urlString) throws MalformedURLException {
        if (urlString.startsWith(DATA_URL_PREFIX)) {
            String dataUuid = StringUtils.substringAfter(urlString, DATA_URL_PREFIX);
            InstancesEntity instancesEntity = instancesRepository.findByUuid(dataUuid).orElseThrow(
                    () -> new EntityNotFoundException(InstancesEntity.class, dataUuid));
            return new FileResource(new File(instancesEntity.getAbsolutePath()));
        }
        return new UrlResource(new URL(urlString));
    }
}
