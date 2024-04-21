package com.ecaservice.data.loader.service;

import com.ecaservice.common.web.exception.FileProcessingException;
import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.common.web.exception.InvalidFileException;
import com.ecaservice.data.loader.config.AppProperties;
import com.ecaservice.data.loader.dto.UploadInstancesResponseDto;
import com.ecaservice.data.loader.entity.InstancesEntity;
import com.ecaservice.data.loader.repository.InstancesRepository;
import com.ecaservice.data.loader.validation.InstancesValidationService;
import com.ecaservice.s3.client.minio.exception.ObjectStorageException;
import com.ecaservice.s3.client.minio.model.UploadObject;
import com.ecaservice.s3.client.minio.service.MinioStorageService;
import eca.data.file.model.InstancesModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Instances upload service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadInstancesService {

    private static final String INSTANCES_OBJECT_PATH_FORMAT = "instances-%s.json";
    private static final String JSON_EXTENSION = "json";

    private final AppProperties appProperties;
    private final MinioStorageService minioStorageService;
    private final InstancesReader instancesReader;
    private final InstancesDeserializer instancesDeserializer;
    private final InstancesValidationService instancesValidationService;
    private final UserService userService;
    private final InstancesRepository instancesRepository;

    /**
     * Uploads instances to storage.
     *
     * @param instancesFile - instances file
     * @return upload response dto
     */
    @NewSpan
    public UploadInstancesResponseDto uploadInstances(MultipartFile instancesFile) {
        String fileExtension = FilenameUtils.getExtension(instancesFile.getOriginalFilename());
        if (!JSON_EXTENSION.equals(fileExtension)) {
            throw new InvalidFileException(String.format("Invalid file [%s] extension. File must be in json format",
                    instancesFile.getOriginalFilename()));
        }
        try {
            String clientId = userService.getCurrentUser();
            byte[] jsonData = instancesReader.readData(instancesFile);
            String md5Hash = DigestUtils.md5DigestAsHex(jsonData);
            var instancesEntity = instancesRepository.findByClientIdAndMd5Hash(clientId, md5Hash);
            if (instancesEntity != null) {
                log.info(
                        "Instances file [{}] with md5 hash [{}] is already uploaded for client id [{}]. Instances uuid [{}]",
                        instancesFile.getOriginalFilename(), md5Hash, clientId, instancesEntity.getUuid());
            } else {
                InstancesModel instancesModel = instancesDeserializer.deserialize(jsonData);
                instancesValidationService.validate(instancesModel);
                String uuid = UUID.randomUUID().toString();
                String objectPath = String.format(INSTANCES_OBJECT_PATH_FORMAT, uuid);
                log.info("Starting to upload instances file [{}] with uuid [{}], md5 hash [{}] object path [{}]",
                        instancesFile.getOriginalFilename(), uuid, md5Hash, objectPath);
                uploadInstancesToS3(objectPath, jsonData);
                instancesEntity = createAndSaveInstancesEntity(uuid, objectPath, md5Hash, clientId, instancesModel);
                log.info("Instances file [{}] has been uploaded with uuid [{}], md5 hash [{}] object path [{}]",
                        instancesFile.getOriginalFilename(), uuid, md5Hash, objectPath);
            }
            return UploadInstancesResponseDto.builder()
                    .uuid(instancesEntity.getUuid())
                    .md5Hash(md5Hash)
                    .expireAt(instancesEntity.getExpireAt())
                    .build();
        } catch (IOException ex) {
            log.error("There was an error while load data from file {}: {}", instancesFile.getOriginalFilename(),
                    ex.getMessage());
            throw new FileProcessingException(ex.getMessage());
        } catch (ObjectStorageException ex) {
            log.error("Object storage error while load data from file {}: {}", instancesFile.getOriginalFilename(),
                    ex.getMessage());
            throw new InternalServiceUnavailableException(ex.getMessage());
        }
    }

    private InstancesEntity createAndSaveInstancesEntity(String uuid,
                                                         String objectPath,
                                                         String md5Hash,
                                                         String clientId,
                                                         InstancesModel instancesModel) {
        var instancesEntity = new InstancesEntity();
        instancesEntity.setRelationName(instancesModel.getRelationName());
        instancesEntity.setNumInstances(instancesModel.getInstances().size());
        instancesEntity.setNumAttributes(instancesModel.getAttributes().size());
        var classAttribute = instancesModel.getAttributes()
                .stream()
                .filter(attributeModel -> attributeModel.getName().equals(instancesModel.getClassName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Class attribute [%s] not found in instances [%s] attributes set",
                                instancesModel.getClassName(), instancesModel.getRelationName())));
        instancesEntity.setNumClasses(classAttribute.getValues().size());
        instancesEntity.setClassName(instancesModel.getClassName());
        instancesEntity.setUuid(uuid);
        instancesEntity.setObjectPath(objectPath);
        instancesEntity.setMd5Hash(md5Hash);
        instancesEntity.setClientId(clientId);
        instancesEntity.setExpireAt(LocalDateTime.now().plusDays(appProperties.getInstancesExpireDays()));
        instancesEntity.setCreated(LocalDateTime.now());
        return instancesRepository.save(instancesEntity);
    }

    private void uploadInstancesToS3(String objectPath, byte[] instances) {
        minioStorageService.uploadObject(
                UploadObject.builder()
                        .objectPath(objectPath)
                        .inputStream(() -> new ByteArrayInputStream(instances))
                        .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .build()
        );
    }
}
