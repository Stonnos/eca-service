package com.ecaservice.data.loader.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.model.InstancesModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Instances deserializer.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesDeserializer {

    private final ObjectMapper objectMapper;

    /**
     * Deserializes instances from byte array.
     *
     * @param data - instances json byte array
     * @return instances model
     * @throws IOException in case of I/O error
     */
    @NewSpan
    public InstancesModel deserialize(byte[] data) throws IOException {
        log.info("Starting to deserialize instances");
        InstancesModel instancesModel = objectMapper.readValue(data, InstancesModel.class);
        log.info("Instances [{}] has been deserialized", instancesModel.getRelationName());
        return instancesModel;
    }
}
