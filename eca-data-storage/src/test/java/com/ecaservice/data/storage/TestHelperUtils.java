package com.ecaservice.data.storage;

import com.ecaservice.data.storage.entity.InstancesEntity;
import eca.data.file.resource.FileResource;
import eca.data.file.xls.XLSLoader;
import lombok.experimental.UtilityClass;
import weka.core.Instances;

import java.io.File;
import java.time.LocalDateTime;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String DATA_PATH = "german_credit.xls";
    private static final String TABLE_NAME = "table";
    private static final String CREATED_BY = "user";
    private static final int NUM_INSTANCES = 100;
    private static final int NUM_ATTRIBUTES = 14;

    /**
     * Loads test data set.
     *
     * @return created training data
     */
    public static Instances loadInstances() throws Exception {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        XLSLoader dataLoader = new XLSLoader();
        dataLoader.setSource(new FileResource(new File(classLoader.getResource(DATA_PATH).getFile())));
        return dataLoader.loadInstances();
    }

    /**
     * Creates instances entity.
     *
     * @return instances entity
     */
    public static InstancesEntity createInstancesEntity() {
        InstancesEntity instancesEntity = new InstancesEntity();
        instancesEntity.setTableName(TABLE_NAME);
        instancesEntity.setNumInstances(NUM_INSTANCES);
        instancesEntity.setNumAttributes(NUM_ATTRIBUTES);
        instancesEntity.setCreated(LocalDateTime.now());
        instancesEntity.setCreatedBy(CREATED_BY);
        return instancesEntity;
    }
}
