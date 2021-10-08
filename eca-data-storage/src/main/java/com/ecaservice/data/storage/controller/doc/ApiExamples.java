package com.ecaservice.data.storage.controller.doc;

import lombok.experimental.UtilityClass;

/**
 * Api examples utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ApiExamples {

    /**
     * Simple page request json
     */
    public static final String SIMPLE_PAGE_REQUEST_JSON = "{\"page\":0,\"size\":25}";

    /**
     * Instances response json
     */
    public static final String INSTANCES_DETAILS_RESPONSE_JSON = "{\"id\": 1, \"tableName\": \"iris\", " +
            "\"numInstances\": 150, \"numAttributes\": 5, \"created\": \"2021-07-01 14:00:00\", \"createdBy\": " +
            "\"admin\"}";

    /**
     * Instances page response json
     */
    public static final String INSTANCES_PAGE_RESPONSE_JSON = "{\"content\": [{\"id\": 1, \"tableName\": \"iris\", " +
            "\"numInstances\": 150, \"numAttributes\": 5, \"created\": \"2021-07-01 14:00:00\", \"createdBy\": " +
            "\"admin\"}], \"page\": 0, \"totalCount\": 1}";

    /**
     * Instances reports info response json
     */
    public static final String INSTANCES_REPORTS_RESPONSE_JSON = "[{\"title\": \"Microsoft Excel (.xlsx)\", " +
            "\"reportType\": \"XLS\", \"fileExtension\": \"xlsx\"}, {\"title\": \"Формат CSV (.csv)\", " +
            "\"reportType\": \"CSV\", \"fileExtension\": \"csv\"}, {\"title\": \"Формат Arff (.arff)\", " +
            "\"reportType\": \"ARFF\", \"fileExtension\": \"arff\"}, {\"title\": \"Json формат (.json)\", " +
            "\"reportType\": \"JSON\", \"fileExtension\": \"json\"}, {\"title\": \"Xml формат (.xml)\", " +
            "\"reportType\": \"XML\", \"fileExtension\": \"xml\"}, {\"title\": \"Текстовый формат (.txt)\", " +
            "\"reportType\": \"TXT\", \"fileExtension\": \"txt\"}, {\"title\": \"Формат данных (.data)\", " +
            "\"reportType\": \"DATA\", \"fileExtension\": \"data\"}, {\"title\": \"Microsoft Word (.docx)\", " +
            "\"reportType\": \"DOCX\", \"fileExtension\": \"docx\"}]";

    /**
     * Create instances response json
     */
    public static final String CREATE_INSTANCES_RESPONSE_JSON = "{\"id\": 1, \"sourceFileName\": \"iris.xls\", " +
            "\"tableName\": \"iris\"}";

    /**
     * Data not found response json
     */
    public static final String UNIQUE_TABLE_ERROR_RESPONSE_JSON = "[{\"fieldName\": null, \"code\": " +
            "\"UniqueTableName\", \"errorMessage\": \"Table with name [iris] already exists!\"}]";

    /**
     * Gets attributes list response json
     */
    public static final String ATTRIBUTES_LIST_RESPONSE_JSON = "[\"x1\", \"x2\", \"x3\", \"x4\", \"class\"]";

    /**
     * Instances data page response json
     */
    public static final String INSTANCES_DATA_PAGE_RESPONSE_JSON = "{\"content\": [[\"5.1\", \"3.5\", \"1.4\", \"0" +
            ".2\", \"Iris-setosa\"], [\"4.9\", \"3.0\", \"1.4\", \"0.2\", \"Iris-setosa\"], [\"4.7\", \"3.2\", " +
            "\"1.3\", \"0.2\", \"Iris-setosa\"]], \"page\": 0, \"totalCount\": 3}";
}
