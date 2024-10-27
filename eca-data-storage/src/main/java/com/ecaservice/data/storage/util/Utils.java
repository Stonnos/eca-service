package com.ecaservice.data.storage.util;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.AttributeValueEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import weka.core.Attribute;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
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

    private static final String SELECT_QUERY = "select %s from %s order by %s";
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
     * Creates nominal attributes values index map.
     *
     * @param attributeEntities - attribute entities
     * @return index map
     */
    public static Map<String, Map<String, Integer>> createNominalAttributesIndexMap(
            List<AttributeEntity> attributeEntities) {
        return attributeEntities.stream()
                .filter(attributeEntity -> AttributeType.NOMINAL.equals(attributeEntity.getType()))
                .collect(Collectors.toMap(
                        AttributeEntity::getAttributeName,
                        attributeEntity -> attributeEntity.getValues().stream().collect(
                                Collectors.toMap(AttributeValueEntity::getValue, AttributeValueEntity::getIndex))
                ));
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
                    attributeValueEntity.setIndex(i);
                    return attributeValueEntity;
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets attribute value with specified code.
     *
     * @param attributeEntity - attribute entity
     * @param code            - attribute value code
     * @return attribute value entity
     */
    public static AttributeValueEntity getAttributeValueByCode(AttributeEntity attributeEntity, String code) {
        return attributeEntity.getValues()
                .stream()
                .filter(attributeValueEntity -> attributeValueEntity.getValue().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Can't find attribute [%s] value [%s]", attributeEntity.getAttributeName(),
                                code)));
    }

    /**
     * Builds sql select query with specified columns sorted by id column.
     *
     * @param instancesEntity - instances entity
     * @param columns         - columns list
     * @return sql select query
     */
    public static String buildSqlSelectQuery(InstancesEntity instancesEntity, List<String> columns) {
        String attributes = StringUtils.join(columns, COMMA_SEPARATOR);
        return String.format(SELECT_QUERY, attributes, instancesEntity.getTableName(),
                instancesEntity.getIdColumnName());
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

    /**
     * Converts to decimal value using half up rounding mode.
     *
     * @param value - double value
     * @param scale - scale value
     * @return decimal value
     */
    public static BigDecimal toDecimal(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP);
    }
}
