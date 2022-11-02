package com.ecaservice.external.api.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.exception.DataNotFoundException;
import com.ecaservice.external.api.exception.ProcessFileException;
import com.ecaservice.external.api.model.MultipartFileResource;
import com.ecaservice.external.api.repository.InstancesRepository;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.UrlResource;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import weka.core.Instances;

import java.io.IOException;
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

    private static final String TRAIN_DATA_MODEL_PATH_FORMAT = "train-data-%s.model";

    private final FileDataLoader fileDataLoader;
    private final ObjectStorageService objectStorageService;
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
        log.info("Starting to upload train data [{}] to file system", trainingData.getOriginalFilename());
        String dataUuid = UUID.randomUUID().toString();
        String dataPath = String.format(TRAIN_DATA_MODEL_PATH_FORMAT, dataUuid);
        var instances = loadInstances(trainingData);
        objectStorageService.uploadObject(instances, dataPath);
        var instancesEntity = new InstancesEntity();
        instancesEntity.setDataPath(dataPath);
        instancesEntity.setUuid(dataUuid);
        instancesEntity.setCreationDate(LocalDateTime.now());
        instancesRepository.save(instancesEntity);
        log.info("Train data [{}] has been uploaded to file system with uuid [{}]",
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
        log.info("Starting to load instances from url: [{}]", url);
        try {
            if (url.startsWith(DATA_URL_PREFIX)) {
                String dataUuid = StringUtils.substringAfter(url, DATA_URL_PREFIX);
                var instancesEntity = instancesRepository.findByUuid(dataUuid)
                        .orElseThrow(() -> new EntityNotFoundException(InstancesEntity.class, dataUuid));
                var instances = objectStorageService.getObject(instancesEntity.getDataPath(), Instances.class);
                log.info("Instances [{}] has been loaded from S3 storage", url);
                return instances;
            } else {
                var urlResource =  new UrlResource(new URL(url));
                fileDataLoader.setSource(urlResource);
                var instances = fileDataLoader.loadInstances();
                log.info("Instances [{}] has been loaded from external url resource", url);
                return instances;
            }
        } catch (Exception ex) {
            throw new DataNotFoundException(ex.getMessage());
        }
    }

    /**
     * Deletes instances entity.
     *
     * @param instancesEntity - instances entity
     */
    public void deleteInstances(InstancesEntity instancesEntity) {
        try {
            log.info("Starting to delete instances [{}]", instancesEntity.getId());
            objectStorageService.removeObject(instancesEntity.getDataPath());
            instancesRepository.delete(instancesEntity);
            log.info("Instances [{}] has been deleted", instancesEntity.getId());
        } catch (Exception ex) {
            log.error("There was an error while deleting instances [{}]: {}", instancesEntity.getId(),
                    ex.getMessage());
        }
    }

    private Instances loadInstances(MultipartFile multipartFile) {
        try {
            var dataResource = new MultipartFileResource(multipartFile);
            fileDataLoader.setSource(dataResource);
            return fileDataLoader.loadInstances();
        } catch (Exception ex) {
            throw new ProcessFileException(ex.getMessage());
        }
    }
}
