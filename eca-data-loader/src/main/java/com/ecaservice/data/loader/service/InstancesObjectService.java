package com.ecaservice.data.loader.service;

import com.ecaservice.data.loader.config.EcaDataLoaderProperties;
import com.ecaservice.data.loader.entity.InstancesObject;
import com.ecaservice.data.loader.repository.InstancesObjectRepository;
import com.ecaservice.s3.client.minio.model.UploadObject;
import com.ecaservice.s3.client.minio.service.MinioStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.data.loader.util.Utils.copyToFile;
import static com.ecaservice.data.loader.util.Utils.createFileInputStreamQuietly;
import static com.ecaservice.data.loader.util.Utils.md5Hash;
import static org.apache.commons.io.FileUtils.delete;

/**
 * Instances object service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesObjectService {

    private static final String TEMP_FILE_NAME_FORMAT = "instances-%s.tmp";

    private final EcaDataLoaderProperties ecaDataLoaderProperties;
    private final MinioStorageService minioStorageService;
    private final InstancesObjectRepository instancesObjectRepository;

    /**
     * Uploads instances file to s3 storage.
     *
     * @param instancesFile - instances multipart file
     * @param objectPath    - object path in s3
     * @return instances object entity
     * @throws IOException in case of I/O errors
     */
    public InstancesObject uploadObject(MultipartFile instancesFile, String objectPath) throws IOException {
        log.info("Starting to upload instances object [{}] to s3", objectPath);
        String tempFileName = String.format(TEMP_FILE_NAME_FORMAT, UUID.randomUUID());
        String tempFilePath = String.format("%s/%s", ecaDataLoaderProperties.getTempFilesLocation(), tempFileName);
        copyToFile(instancesFile, tempFilePath);
        log.debug("Temp file [{}] has been created for object path [{}]", tempFilePath, objectPath);
        String md5Hash = md5Hash(tempFilePath);
        log.debug("Temp file [{}] md5 hash [{}] for object path [{}]", instancesFile, md5Hash, objectPath);
        uploadInstancesToS3(objectPath, tempFilePath);
        delete(new File(tempFilePath));
        log.debug("Temp file [{}] has been deleted for object path [{}]", tempFilePath, objectPath);
        var instancesObject = new InstancesObject();
        instancesObject.setMd5Hash(md5Hash);
        instancesObject.setObjectPath(objectPath);
        instancesObject.setCreated(LocalDateTime.now());
        instancesObjectRepository.save(instancesObject);
        log.info("Instances object [{}] has been uploaded to s3", objectPath);
        return instancesObject;
    }

    private void uploadInstancesToS3(String objectPath, String fileName) {
        minioStorageService.uploadObject(
                UploadObject.builder()
                        .objectPath(objectPath)
                        .inputStream(() -> createFileInputStreamQuietly(fileName))
                        .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .build()
        );
    }
}
