package com.ecaservice.server.service.data;

import com.ecaservice.s3.client.minio.databind.JsonDeserializer;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.service.InstancesInfoService;
import eca.data.file.converter.InstancesConverter;
import eca.data.file.model.InstancesModel;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.io.IOException;

/**
 * Service for loading instances from storage.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesLoaderService {

    private final ObjectStorageService objectStorageService;
    private final InstancesInfoService instancesInfoService;
    private final InstancesConverter instancesConverter = new InstancesConverter();
    private final JsonDeserializer<InstancesModel> instancesModelJsonDeserializer = new JsonDeserializer<>();

    /**
     * Loads instances with specified uuid.
     *
     * @param uuid - instances uuid
     * @return instances
     */
    @NewSpan
    public Instances loadInstances(String uuid) {
        log.info("Starting to load instances [{}] from data storage", uuid);
        var instancesInfo = instancesInfoService.getOrSaveInstancesInfo(uuid);
        var instancesModel = getInstancesModel(instancesInfo.getObjectPath());
        var instances = instancesConverter.convert(instancesModel);
        log.info("Instances [{}] has been loaded from data storage", uuid);
        return instances;
    }

    private InstancesModel getInstancesModel(String objectPath) {
        try {
            return objectStorageService.getObject(objectPath, InstancesModel.class, instancesModelJsonDeserializer);
        } catch (IOException | ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
