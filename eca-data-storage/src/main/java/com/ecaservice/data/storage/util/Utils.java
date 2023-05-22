package com.ecaservice.data.storage.util;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.AttributeValueEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import weka.core.Attribute;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static eca.data.db.SqlQueryHelper.formatNominalValue;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    /**
     * Min. number of classes
     */
    public static final int MIN_NUM_CLASSES = 2;

    /**
     * Min. number of selected attributes
     */
    public static final int MIN_NUM_SELECTED_ATTRIBUTES = 2;

    private static final String SELECT_QUERY = "select %s from %s";
    private static final String COMMA_SEPARATOR = ",";
    private static final String SELECT_COUNT_DISTINCT_QUERY = "select count(distinct %s) from %s";

    /**
     * Gets attribute type.
     *
     * @param attribute - attribute
     * @return attribute type
     */
    public static AttributeType getAttributeType(Attribute attribute) {
        if (attribute.isNominal()) {
            return AttributeType.NOMINAL;
        } else if (attribute.isDate()) {
            return AttributeType.DATE;
        } else if (attribute.isNumeric()) {
            return AttributeType.NUMERIC;
        } else {
            throw new IllegalArgumentException(String.format("Unexpected attribute '%s' type!", attribute.name()));
        }
    }

    /**
     * Gets attribute values entities.
     *
     * @param attribute - attribute
     * @return attribute values entities
     */
    public static List<AttributeValueEntity> getAttributeValues(Attribute attribute) {
        return IntStream.range(0, attribute.numValues())
                .mapToObj(i -> {
                    var attributeValueEntity = new AttributeValueEntity();
                    String value = formatNominalValue(attribute.value(i));
                    attributeValueEntity.setValue(value);
                    attributeValueEntity.setValueOrder(i);
                    return attributeValueEntity;
                })
                .collect(Collectors.toList());
    }

    /**
     * Builds sql select query with specified attributes.
     *
     * @param tableName         - table name
     * @param attributeEntities - attributes entities
     * @return sql select query
     */
    public static String buildSqlSelectQuery(String tableName, List<AttributeEntity> attributeEntities) {
        var values = attributeEntities.stream()
                .map(AttributeEntity::getColumnName)
                .collect(Collectors.toList());
        String attributes = StringUtils.join(values, COMMA_SEPARATOR);
        return String.format(SELECT_QUERY, attributes, tableName);
    }

    /**
     * Builds sql count unique values.
     *
     * @param tableName  - table name
     * @param columnName - column name
     * @return sql query
     */
    public static String buildSqlCountUniqueValuesQuery(String tableName, String columnName) {
        return String.format(SELECT_COUNT_DISTINCT_QUERY, columnName, tableName);
    }
}
