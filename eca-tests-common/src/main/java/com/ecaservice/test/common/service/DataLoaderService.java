package com.ecaservice.test.common.service;

import com.ecaservice.common.web.multipart.ByteArrayMultipartFile;
import com.ecaservice.data.loader.dto.UploadInstancesResponseDto;
import com.ecaservice.test.common.exception.DataLoaderException;
import com.ecaservice.test.common.service.api.DataLoaderApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ecaservice.test.common.util.Utils.copyToByteArray;

/**
 * Data loader service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataLoaderService {

    private final DataLoaderApiClient dataLoaderApiClient;
    private final Map<String, String> uploadedInstancesCache = new ConcurrentHashMap<>();
    private final Map<String, Object> monitorsMap = new ConcurrentHashMap<>();

    /**
     * Uploads instances to data storage.
     *
     * @param resource - instances resource
     * @return instances uuid
     */
    public String uploadInstances(Resource resource) {
        String fileName = resource.getFilename();
        String dataUuid;
        log.info("Starting to upload instances file [{}] to data storage", fileName);
        monitorsMap.putIfAbsent(fileName, new Object());
        try {
            synchronized (monitorsMap.get(fileName)) {
                dataUuid = uploadedInstancesCache.get(fileName);
                if (!StringUtils.isEmpty(dataUuid)) {
                    log.info("Instances file [{}] uuid [{}] has been taken from cache", fileName, dataUuid);
                } else {
                    dataUuid = internalUploadInstances(resource);
                }
            }
        } catch (IOException ex) {
            log.error("Error while upload instances file [{}] to data storage: {}", fileName, ex.getMessage());
            throw new DataLoaderException(ex.getMessage());
        } finally {
            monitorsMap.remove(fileName);
        }
        return dataUuid;
    }

    private String internalUploadInstances(Resource resource) throws IOException {
        String fileName = resource.getFilename();
        byte[] array = copyToByteArray(resource);
        MultipartFile trainDataFile =
                new ByteArrayMultipartFile(resource.getFilename(), resource.getFilename(), array);
        UploadInstancesResponseDto uploadInstancesResponseDto =
                dataLoaderApiClient.uploadInstances(trainDataFile);
        log.info("Instances file [{}] has been uploaded to data storage with uuid [{}]", fileName,
                uploadInstancesResponseDto.getUuid());
        uploadedInstancesCache.put(fileName, uploadInstancesResponseDto.getUuid());
        return uploadInstancesResponseDto.getUuid();
    }
}
