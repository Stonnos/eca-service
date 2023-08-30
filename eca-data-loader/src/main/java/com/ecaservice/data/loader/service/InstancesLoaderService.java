package com.ecaservice.data.loader.service;

import com.ecaservice.common.web.exception.FileProcessingException;
import com.ecaservice.common.web.exception.InvalidFileException;
import com.ecaservice.data.loader.dto.UploadInstancesResponseDto;
import com.ecaservice.data.loader.entity.InstancesEntity;
import com.ecaservice.data.loader.entity.InstancesObject;
import com.ecaservice.data.loader.repository.InstancesRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.model.InstancesModel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.data.loader.util.Utils.validateInstances;

/**
 * Instances loader service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesLoaderService {

    private static final String INSTANCES_OBJECT_PATH_FORMAT = "instances-%s.json";
    private static final String JSON_EXTENSION = "json";

    private final InstancesObjectService instancesObjectService;
    private final ObjectMapper objectMapper;
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
            validateInstances(instancesModel);
            String uuid = UUID.randomUUID().toString();
            String objectPath = String.format(INSTANCES_OBJECT_PATH_FORMAT, uuid);
            log.info("Starting to upload instances file [{}] with uuid [{}], object path [{}]",
                    instancesFile.getOriginalFilename(), uuid, objectPath);
            var instancesObject = instancesObjectService.uploadObject(jsonData, objectPath);
            createAndSaveInstancesEntity(uuid, instancesObject, instancesModel);
            log.info("Instances file [{}] has been uploaded with uuid [{}], object path [{}]",
                    instancesFile.getOriginalFilename(), uuid, objectPath);
            return UploadInstancesResponseDto.builder()
                    .uuid(uuid)
                    .build();
        } catch (IOException ex) {
            log.error("There was an error while load data from file {}: {}", instancesFile.getOriginalFilename(),
                    ex.getMessage());
            throw new FileProcessingException(ex.getMessage());
        }
    }

    private void createAndSaveInstancesEntity(String uuid,
                                              InstancesObject instancesObject,
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
        instancesEntity.setCreated(LocalDateTime.now());
        instancesEntity.setInstancesObject(instancesObject);
        instancesRepository.save(instancesEntity);
    }

    private byte[] loadData(MultipartFile instancesFile) throws IOException {
        @Cleanup var inputStream = instancesFile.getInputStream();
        return IOUtils.toByteArray(inputStream);
    }
}
