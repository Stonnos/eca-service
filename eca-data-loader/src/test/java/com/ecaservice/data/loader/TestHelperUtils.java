package com.ecaservice.data.loader;

import com.ecaservice.data.loader.entity.InstancesEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.model.InstancesModel;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String INSTANCES_FILE_PARAM = "instancesFile";
    private static final String CREDIT_JSON = "data/credit.json";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String RELATION_NAME = "relationName";
    private static final String CLASS_NAME = "class";
    private static final int NUM_INSTANCES = 1000;
    private static final int NUM_ATTRIBUTES = 12;
    private static final int NUM_CLASSES = 2;
    private static final String OBJECT_PATH = "instances-1d2de514-3a87-4620-9b97-c260e24340de.json";
    private static final String MD_5_HASH = "3032e188204cb537f69fc7364f638641";

    /**
     * Creates instances mock multipart file.
     *
     * @return instances mock multipart file
     */
    @SneakyThrows
    public static MockMultipartFile createInstancesMockMultipartFile() {
        return createInstancesMockMultipartFile(CREDIT_JSON);
    }

    /**
     * Creates instances mock multipart file.
     *
     * @param fileName - file name
     * @return instances mock multipart file
     */
    @SneakyThrows
    public static MockMultipartFile createInstancesMockMultipartFile(String fileName) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        @Cleanup InputStream inputStream = classLoader.getResourceAsStream(CREDIT_JSON);
        return new MockMultipartFile(INSTANCES_FILE_PARAM, fileName, null, inputStream);
    }

    /**
     * Loads instances model from resource.
     *
     * @return instances model
     * @throws IOException in case of I/O error
     */
    public static InstancesModel loadInstances() throws IOException {
        return loadInstances(CREDIT_JSON);
    }

    /**
     * Loads instances model from resource.
     *
     * @param location - location
     * @return instances model
     * @throws IOException in case of I/O error
     */
    public static InstancesModel loadInstances(String location) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        @Cleanup InputStream inputStream = classLoader.getResourceAsStream(location);
        return OBJECT_MAPPER.readValue(inputStream, InstancesModel.class);
    }

    /**
     * Creates instances entity.
     *
     * @return instances entity
     */
    public static InstancesEntity createInstancesEntity() {
        InstancesEntity instancesEntity = new InstancesEntity();
        instancesEntity.setUuid(UUID.randomUUID().toString());
        instancesEntity.setRelationName(RELATION_NAME);
        instancesEntity.setClassName(CLASS_NAME);
        instancesEntity.setNumInstances(NUM_INSTANCES);
        instancesEntity.setNumAttributes(NUM_ATTRIBUTES);
        instancesEntity.setNumClasses(NUM_CLASSES);
        instancesEntity.setObjectPath(OBJECT_PATH);
        instancesEntity.setMd5Hash(MD_5_HASH);
        instancesEntity.setCreated(LocalDateTime.now());
        instancesEntity.setExpireAt(LocalDateTime.now().plusDays(1L));
        return instancesEntity;
    }

    /**
     * Creates instances entity.
     *
     * @param expireAt - expire at date
     * @return instances entity
     */
    public static InstancesEntity createInstancesEntity(LocalDateTime expireAt) {
        InstancesEntity instancesEntity = createInstancesEntity();
        instancesEntity.setExpireAt(expireAt);
        return instancesEntity;
    }
}
