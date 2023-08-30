package com.ecaservice.data.loader.service;

import com.ecaservice.data.loader.entity.InstancesObject;
import com.ecaservice.data.loader.repository.InstancesObjectRepository;
import com.ecaservice.s3.client.minio.model.UploadObject;
import com.ecaservice.s3.client.minio.service.MinioStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

/**
 * Instances object service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesObjectService {

    private final MinioStorageService minioStorageService;
    private final InstancesObjectRepository instancesObjectRepository;

    /**
     * Uploads instances file to s3 storage.
     *
     * @param instances - instances data
     * @param objectPath    - object path in s3
     * @return instances object entity
     */
    public InstancesObject uploadObject(byte[] instances, String objectPath) {
        log.info("Starting to upload instances object [{}] to s3", objectPath);
        String md5Hash = DigestUtils.md5DigestAsHex(instances);
        uploadInstancesToS3(objectPath, instances);
        var instancesObject = new InstancesObject();
        instancesObject.setMd5Hash(md5Hash);
        instancesObject.setObjectPath(objectPath);
        instancesObject.setCreated(LocalDateTime.now());
        instancesObjectRepository.save(instancesObject);
        log.info("Instances object [{}] has been uploaded to s3", objectPath);
        return instancesObject;
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
