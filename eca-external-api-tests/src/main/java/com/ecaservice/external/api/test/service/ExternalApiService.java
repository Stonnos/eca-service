package com.ecaservice.external.api.test.service;

import com.ecaservice.external.api.dto.InstancesDto;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.test.multipart.ByteArrayMultipartFile;
import com.ecaservice.external.api.test.service.api.ExternalApiClient;
import eca.core.ModelSerializationHelper;
import eca.core.model.ClassificationModel;
import eca.data.file.resource.UrlResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

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
     * @param modelUrl - model url
     * @return classifier model
     * @throws IOException in case of I/O error
     */
    public ClassificationModel downloadModel(String modelUrl) throws IOException {
        URL experimentUrl = new URL(modelUrl);
        return ModelSerializationHelper.deserialize(new UrlResource(experimentUrl), ClassificationModel.class);
    }
}
