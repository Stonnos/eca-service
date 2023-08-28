package com.ecaservice.data.loader.service;

import com.ecaservice.common.web.exception.InvalidFileException;
import com.ecaservice.data.loader.dto.InstancesMetaInfoRequestDto;
import com.ecaservice.data.loader.dto.UploadInstancesResponseDto;
import com.ecaservice.data.loader.mapping.InstancesMapper;
import com.ecaservice.data.loader.repository.InstancesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
public class InstancesLoaderService {

    private static final String INSTANCES_OBJECT_PATH_FORMAT = "instances-%s.json";
    private static final String JSON_EXTENSION = "json";

    private final InstancesMapper instancesMapper;
    private final InstancesObjectService instancesObjectService;
    private final InstancesRepository instancesRepository;

    /**
     * Uploads instances to storage.
     *
     * @param instancesMetaInfoRequestDto - instances meta info request dto
     * @param instancesFile               - instances file
     * @return upload response dto
     * @throws IOException in case of I/O error
     */
    public UploadInstancesResponseDto uploadInstances(InstancesMetaInfoRequestDto instancesMetaInfoRequestDto,
                                                      MultipartFile instancesFile) throws IOException {
        String fileExtension = FilenameUtils.getExtension(instancesFile.getOriginalFilename());
        if (!JSON_EXTENSION.equals(fileExtension)) {
            throw new InvalidFileException(String.format("Invalid file [%s] extension. File must be in json format",
                    instancesFile.getOriginalFilename()));
        }
        String uuid = UUID.randomUUID().toString();
        String objectPath = String.format(INSTANCES_OBJECT_PATH_FORMAT, uuid);
        log.info("Starting to upload instances file [{}] with uuid [{}], object path [{}]",
                instancesFile.getOriginalFilename(), uuid, objectPath);
        var instancesObject = instancesObjectService.uploadObject(instancesFile, objectPath);
        var instancesEntity = instancesMapper.map(instancesMetaInfoRequestDto);
        instancesEntity.setUuid(uuid);
        instancesEntity.setCreated(LocalDateTime.now());
        instancesEntity.setInstancesObject(instancesObject);
        instancesRepository.save(instancesEntity);
        log.info("Instances file [{}] has been uploaded with uuid [{}], object path [{}]",
                instancesFile.getOriginalFilename(), uuid, objectPath);
        return UploadInstancesResponseDto.builder()
                .uuid(uuid)
                .build();
    }
}
