package com.ecaservice.server.service;

import com.ecaservice.server.service.ds.DataStorageClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.model.InstancesModel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Data storage loader.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataStorageLoader {

    private final DataStorageClient dataStorageClient;
    private final ObjectMapper objectMapper;

    /**
     * Gets valid instances with specified uuid.
     *
     * @param uuid - instances uuid
     * @return instances object
     * @throws IOException in case of I/O errors
     */
    public InstancesModel downloadValidInstancesReport(String uuid) throws IOException {
        var resource = dataStorageClient.downloadValidInstancesReport(uuid);
        @Cleanup var inputStream = resource.getInputStream();
        return objectMapper.readValue(inputStream, InstancesModel.class);
    }
}
