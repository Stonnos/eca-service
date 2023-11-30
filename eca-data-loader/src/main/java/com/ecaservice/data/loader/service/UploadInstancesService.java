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
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.model.InstancesModel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Instances loader service.
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
    private final ObjectMapper objectMapper;
    private final InstancesValidationService instancesValidationService;
    private final InstancesRepository instancesRepository;

    /**
     * Uploads instances to storage.
     *
     * @param instancesFile - instances file
     * @return upload response dto
     */
    public UploadInstancesResponseDto uploadInstances(MultipartFile instancesFile) {
        String fileExtension = FilenameUtils.getExtension(instancesFile.getOriginalFilename());
        if (!JSON_EXTENSION.equals(fileExtension)) {
            throw new InvalidFileException(String.format("Invalid file [%s] extension. File must be in json format",
                    instancesFile.getOriginalFilename()));
        }
        try {
            byte[] jsonData = loadData(instancesFile);
            InstancesModel instancesModel = objectMapper.readValue(jsonData, InstancesModel.class);
            instancesValidationService.validate(instancesModel);
            String uuid = UUID.randomUUID().toString();
            String objectPath = String.format(INSTANCES_OBJECT_PATH_FORMAT, uuid);
            String md5Hash = DigestUtils.md5DigestAsHex(jsonData);
            log.info("Starting to upload instances file [{}] with uuid [{}], object path [{}]",
                    instancesFile.getOriginalFilename(), uuid, objectPath);
            uploadInstancesToS3(objectPath, jsonData);
            var instancesEntity = createAndSaveInstancesEntity(uuid, objectPath, md5Hash, instancesModel);
            log.info("Instances file [{}] has been uploaded with uuid [{}], object path [{}]",
                    instancesFile.getOriginalFilename(), uuid, objectPath);
            return UploadInstancesResponseDto.builder()
                    .uuid(uuid)
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
        instancesEntity.setExpireAt(LocalDateTime.now().plusDays(appProperties.getInstancesExpireDays()));
        instancesEntity.setCreated(LocalDateTime.now());
        return instancesRepository.save(instancesEntity);
    }

    private byte[] loadData(MultipartFile instancesFile) throws IOException {
        @Cleanup var inputStream = instancesFile.getInputStream();
        return IOUtils.toByteArray(inputStream);
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
