package com.ecaservice.external.api.service;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.exception.DataNotFoundException;
import com.ecaservice.external.api.exception.EntityNotFoundException;
import com.ecaservice.external.api.repository.InstancesRepository;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.DataResource;
import eca.data.file.resource.FileResource;
import eca.data.file.resource.UrlResource;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static com.ecaservice.external.api.util.Constants.DATA_URL_PREFIX;

/**
 * Instances service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesService {

    private static final String FILE_PATH_FORMAT = "%s_%s.%s";

    private final ExternalApiConfig externalApiConfig;
    private final FileDataLoader fileDataLoader;
    private final InstancesRepository instancesRepository;

    /**
     * Upload train data file to file system.
     *
     * @param multipartFile - train data file
     * @return instance entity
     * @throws IOException in case of error
     */
    public InstancesEntity uploadInstances(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        log.debug("Starting to upload train data [{}] to file system", fileName);
        @Cleanup InputStream inputStream = multipartFile.getInputStream();
        String dataUuid = UUID.randomUUID().toString();
        String extension = FilenameUtils.getExtension(fileName);
        String baseName = FilenameUtils.getBaseName(fileName);
        String trainDataPath = String.format(FILE_PATH_FORMAT, baseName, dataUuid, extension);
        FileUtils.copyInputStreamToFile(inputStream, new File(externalApiConfig.getTrainDataPath(), trainDataPath));
        InstancesEntity instancesEntity = new InstancesEntity();
        instancesEntity.setAbsolutePath(trainDataPath);
        instancesEntity.setUuid(dataUuid);
        instancesRepository.save(instancesEntity);
        log.debug("Train data [{}] has been uploaded to file system with uuid [{}]", fileName, dataUuid);
        return instancesEntity;
    }

    /**
     * Loads instances for specified url.
     *
     * @param url - url string
     * @return instances object
     */
    public Instances loadInstances(String url) {
        try {
            DataResource<?> dataResource = createDataResource(url);
            fileDataLoader.setSource(dataResource);
            return fileDataLoader.loadInstances();
        } catch (Exception ex) {
            throw new DataNotFoundException(ex.getMessage());
        }

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
