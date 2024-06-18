package com.ecaservice.data.storage;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.AttributeValueEntity;
import com.ecaservice.data.storage.entity.ExportInstancesObjectEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.model.report.ReportProperties;
import com.ecaservice.data.storage.model.report.ReportType;
import com.ecaservice.web.dto.model.AttributeDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.model.InstancesModel;
import eca.data.file.resource.FileResource;
import eca.data.file.xls.XLSLoader;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    public static final String CREDIT_DATA_PATH = "german_credit.xls";

    public static final String IONOSPHERE_DATA_PATH = "ionosphere.xlsx";

    public static final String GLASS_DATA_PATH = "glass.xlsx";

    private static final String TITLE = "title";
    private static final String TABLE_NAME = "table";
    private static final String CREATED_BY = "user";
    private static final int NUM_INSTANCES = 100;
    private static final int NUM_ATTRIBUTES = 14;

    private static final int PAGE_SIZE = 10;
    private static final int PAGE_NUMBER = 0;
    private static final int NUM_ATTR_VALUES = 3;
    private static final String ID_COLUMN_NAME = "ID";

    private static final String MD_5_HASH = "3032e188204cb537f69fc7364f638641";

    private static final String CREDIT_JSON = "credit.json";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Loads instances model from resource.
     *
     * @return instances model
     * @throws IOException in case of I/O error
     */
    public static InstancesModel loadInstancesModel() throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        @Cleanup InputStream inputStream = classLoader.getResourceAsStream(CREDIT_JSON);
        return OBJECT_MAPPER.readValue(inputStream, InstancesModel.class);
    }

    /**
     * Creates page request dto.
     *
     * @return page request dto
     */
    public static PageRequestDto createPageRequestDto() {
        return new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, Collections.emptyList(), null, Collections.emptyList());
    }

    /**
     * Loads test data set.
     *
     * @return created training data
     */
    @SneakyThrows
    public static Instances loadInstances() {
        return loadInstances(CREDIT_DATA_PATH);
    }

    /**
     * Loads test data set.
     *
     * @param path - file path
     * @return created training data
     */
    @SneakyThrows
    public static Instances loadInstances(String path) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        XLSLoader dataLoader = new XLSLoader();
        dataLoader.setSource(new FileResource(new File(classLoader.getResource(path).getFile())));
        return dataLoader.loadInstances();
    }

    /**
     * Creates instances entity.
     *
     * @return instances entity
     */
    public static InstancesEntity createInstancesEntity() {
        InstancesEntity instancesEntity = new InstancesEntity();
        instancesEntity.setUuid(UUID.randomUUID().toString());
        instancesEntity.setIdColumnName(ID_COLUMN_NAME);
        instancesEntity.setRelationName(TABLE_NAME);
        instancesEntity.setTableName(TABLE_NAME);
        instancesEntity.setNumInstances(NUM_INSTANCES);
        instancesEntity.setNumAttributes(NUM_ATTRIBUTES);
        instancesEntity.setCreated(LocalDateTime.now());
        instancesEntity.setCreatedBy(CREATED_BY);
        return instancesEntity;
    }

    /**
     * Creates report properties.
     *
     * @return report properties
     */
    public static ReportProperties createReportProperties() {
        var reportProperties = new ReportProperties();
        reportProperties.setReportType(ReportType.CSV);
        reportProperties.setTitle(TITLE);
        return reportProperties;
    }

    /**
     * Creates attribute entity.
     *
     * @param columnName - column name
     * @param index      - attribute index
     * @return attribute entity
     */
    public static AttributeEntity createAttributeEntity(String columnName, int index, AttributeType attributeType) {
        var attributeEntity = new AttributeEntity();
        attributeEntity.setColumnName(columnName);
        attributeEntity.setAttributeName(columnName);
        attributeEntity.setIndex(index);
        attributeEntity.setSelected(true);
        attributeEntity.setType(attributeType);
        return attributeEntity;
    }

    /**
     * Creates nominal attribute entity.
     *
     * @param columnName - column name
     * @param index      - attribute index
     * @return attribute entity
     */
    public static AttributeEntity createNominalAttributeEntity(String columnName, int index) {
        var attributeEntity = new AttributeEntity();
        attributeEntity.setColumnName(columnName);
        attributeEntity.setAttributeName(columnName);
        attributeEntity.setIndex(index);
        attributeEntity.setSelected(true);
        attributeEntity.setType(AttributeType.NOMINAL);
        var values = IntStream.range(0, NUM_ATTR_VALUES)
                .mapToObj(i -> createAttributeValueEntity(String.valueOf(i), i))
                .collect(Collectors.toList());
        attributeEntity.setValues(values);
        return attributeEntity;
    }

    /**
     * Creates attribute value entity.
     *
     * @param value - attribute value
     * @param index - value index
     * @return attribute value entity
     */
    public static AttributeValueEntity createAttributeValueEntity(String value, int index) {
        var attributeValueEntity = new AttributeValueEntity();
        attributeValueEntity.setValue(value);
        attributeValueEntity.setIndex(index);
        return attributeValueEntity;
    }

    /**
     * Creates attribute dto.
     *
     * @param name - attribute name
     * @return attribute dto
     */
    public static AttributeDto createAttributeDto(String name) {
        var attributeDto = new AttributeDto();
        attributeDto.setName(name);
        attributeDto.setSelected(true);
        return attributeDto;
    }

    /**
     * Creates export instances entity object.
     *
     * @param instancesUuid - instances uuid
     * @param expireAt      - expiration date
     * @return export instances entity
     */
    public static ExportInstancesObjectEntity createExportInstancesObjectEntity(String instancesUuid,
                                                                                LocalDateTime expireAt) {
        ExportInstancesObjectEntity exportInstancesObjectEntity = new ExportInstancesObjectEntity();
        exportInstancesObjectEntity.setInstancesUuid(instancesUuid);
        exportInstancesObjectEntity.setExternalDataUuid(UUID.randomUUID().toString());
        exportInstancesObjectEntity.setExpireAt(expireAt);
        exportInstancesObjectEntity.setMd5Hash(MD_5_HASH);
        exportInstancesObjectEntity.setCreated(LocalDateTime.now());
        return exportInstancesObjectEntity;
    }

    /**
     * Calculate num attributes with specified type.
     *
     * @param instances - instances
     * @param type      - attribute type
     * @return num attributes
     */
    public static int numAttributes(Instances instances, int type) {
        return (int) IntStream.range(0, instances.numAttributes())
                .filter(i -> instances.attribute(i).type() == type)
                .count();
    }
}
