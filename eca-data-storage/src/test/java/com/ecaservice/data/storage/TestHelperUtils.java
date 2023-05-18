package com.ecaservice.data.storage;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.AttributeValueEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.model.report.ReportProperties;
import com.ecaservice.data.storage.model.report.ReportType;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.file.resource.FileResource;
import eca.data.file.xls.XLSLoader;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import weka.core.Instances;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String TITLE = "title";
    private static final String DATA_PATH = "german_credit.xls";
    private static final String TABLE_NAME = "table";
    private static final String CREATED_BY = "user";
    private static final int NUM_INSTANCES = 100;
    private static final int NUM_ATTRIBUTES = 14;
    private static final String BEARER_HEADER_FORMAT = "Bearer %s";

    private static final int PAGE_SIZE = 10;
    private static final int PAGE_NUMBER = 0;
    private static final int NUM_ATTR_VALUES = 3;

    /**
     * Creates page request dto.
     *
     * @return page request dto
     */
    public static PageRequestDto createPageRequestDto() {
        return new PageRequestDto(PAGE_NUMBER, PAGE_SIZE, null, true, null, Collections.emptyList());
    }

    /**
     * Loads test data set.
     *
     * @return created training data
     */
    @SneakyThrows
    public static Instances loadInstances() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        XLSLoader dataLoader = new XLSLoader();
        dataLoader.setSource(new FileResource(new File(classLoader.getResource(DATA_PATH).getFile())));
        return dataLoader.loadInstances();
    }

    /**
     * Creates authorization header with bearer token.
     *
     * @param token - token
     * @return authorization header with bearer token
     */
    public static String bearerHeader(String token) {
        return String.format(BEARER_HEADER_FORMAT, token);
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
     * @param order - value order
     * @return attribute value entity
     */
    public static AttributeValueEntity createAttributeValueEntity(String value, int order) {
        var attributeValueEntity = new AttributeValueEntity();
        attributeValueEntity.setValue(value);
        attributeValueEntity.setValueOrder(order);
        return attributeValueEntity;
    }
}
