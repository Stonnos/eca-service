package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeTypeVisitor;
import com.ecaservice.data.storage.entity.AttributeValueEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ecaservice.data.storage.util.SqlUtils.getStringValueSafe;

/**
 * Implements instances result set extractor.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class InstancesResultSetExtractor implements ResultSetExtractor<Instances> {

    private final InstancesEntity instancesEntity;
    private final List<AttributeEntity> attributeEntities;

    @Getter
    @Setter
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    @Override
    public Instances extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        String tableName = instancesEntity.getTableName();
        ArrayList<Attribute> attributes = createAttributes();
        Instances instances = new Instances(tableName, attributes, instancesEntity.getNumInstances());
        while (resultSet.next()) {
            Instance instance = new DenseInstance(instances.numAttributes());
            for (int i = 1; i <= instances.numAttributes(); i++) {
                Attribute attribute = instances.attribute(i - 1);
                //Skip id column
                int columnIndex = i + 1;
                if (resultSet.getObject(columnIndex) == null) {
                    instance.setValue(attribute, Utils.missingValue());
                } else if (attribute.isDate()) {
                    instance.setValue(attribute, resultSet.getTimestamp(columnIndex).getTime());
                } else if (attribute.isNumeric()) {
                    instance.setValue(attribute, resultSet.getBigDecimal(columnIndex).doubleValue());
                } else {
                    instance.setValue(attribute, getStringValueSafe(resultSet, columnIndex));
                }
            }
            instances.add(instance);
        }
        if (instancesEntity.getClassAttribute() != null) {
            var classAttribute = instances.attribute(instancesEntity.getClassAttribute().getColumnName());
            instances.setClass(classAttribute);
        }
        return instances;
    }

    private ArrayList<Attribute> createAttributes() {
        return IntStream.range(0, instancesEntity.getNumAttributes())
                .mapToObj(i -> {
                    var attributeEntity = attributeEntities.get(i);
                    return createAttribute(attributeEntity);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private Attribute createAttribute(AttributeEntity attributeEntity) {
        return attributeEntity.getType().handle(new AttributeTypeVisitor<>() {
            @Override
            public Attribute caseNumeric() {
                return new Attribute(attributeEntity.getColumnName());
            }

            @Override
            public Attribute caseNominal() {
                var values = attributeEntity.getValues()
                        .stream()
                        .map(AttributeValueEntity::getValue)
                        .collect(Collectors.toList());
                return new Attribute(attributeEntity.getColumnName(), values);
            }

            @Override
            public Attribute caseDate() {
                return new Attribute(attributeEntity.getColumnName(), dateFormat);
            }
        });
    }
}
