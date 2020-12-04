package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.dto.InstancesDto;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.multipart.ByteArrayMultipartFile;
import com.ecaservice.external.api.test.service.api.ExternalApiClient;
import eca.converters.model.ClassificationModel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static com.ecaservice.external.api.test.util.Utils.copyToByteArray;

/**
 * External API service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalApiService {

    private final ExternalApiClient externalApiClient;

    /**
     * Uploads train data to server.
     *
     * @param resource - train data resource
     * @return instances dto
     * @throws IOException in case of I/O error
     */
    public ResponseDto<InstancesDto> uploadInstances(Resource resource) throws IOException {
        byte[] array = copyToByteArray(resource);
        MultipartFile trainDataFile = new ByteArrayMultipartFile(resource.getFilename(), resource.getFilename(), array);
        return externalApiClient.uploadInstances(trainDataFile);
    }

    /**
     * Downloads classifier model.
     *
     * @param requestId - request id
     * @return classifier model
     * @throws IOException in case of I/O error
     */
    public ClassificationModel downloadModel(String requestId) throws IOException {
        Resource modelResource = externalApiClient.downloadModel(requestId);
        @Cleanup InputStream inputStream = modelResource.getInputStream();
        return SerializationUtils.deserialize(inputStream);
    }
}
