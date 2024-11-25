package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeTypeVisitor;
import com.ecaservice.data.storage.entity.AttributeValueEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import eca.data.file.model.AttributeModel;
import eca.data.file.model.AttributeType;
import eca.data.file.model.InstanceModel;
import eca.data.file.model.InstancesModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ecaservice.data.storage.util.Utils.createNominalAttributesIndexMap;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Implements instances model result set extractor.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class InstancesModelResultSetExtractor implements ResultSetExtractor<InstancesModel> {

    private final InstancesEntity instancesEntity;
    private final List<AttributeEntity> attributeEntities;

    @Getter
    @Setter
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    @Override
    public InstancesModel extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        ArrayList<AttributeModel> attributes = createAttributes();
        InstancesModel instancesModel = new InstancesModel();
        instancesModel.setInstances(newArrayList());
        instancesModel.setRelationName(instancesEntity.getRelationName());
        instancesModel.setAttributes(attributes);
        var dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        var valueExtractor = new AttributeValueExtractor(resultSet, dateTimeFormatter);
        Map<String, Map<String, Integer>> nominalAttributeIndexMap = createNominalAttributesIndexMap(attributeEntities);
        while (resultSet.next()) {
            InstanceModel instance = new InstanceModel();
            List<Double> values = newArrayList();
            for (int i = 1; i <= attributeEntities.size(); i++) {
                AttributeEntity attributeEntity = attributeEntities.get(i - 1);
                if (resultSet.getObject(i) == null) {
                    values.add(null);
                } else {
                    valueExtractor.setColumnIndex(i);
                    Double value =
                            getValue(attributeEntity, valueExtractor, dateTimeFormatter, nominalAttributeIndexMap);
                    values.add(value);
                }
            }
            instance.setValues(values);
            instancesModel.getInstances().add(instance);
        }
        if (instancesEntity.getClassAttribute() != null) {
            instancesModel.setClassName(instancesEntity.getClassAttribute().getAttributeName());
        }
        return instancesModel;
    }

    private Double getValue(AttributeEntity attributeEntity,
                            AttributeValueExtractor valueExtractor,
                            DateTimeFormatter dateTimeFormatter,
                            Map<String, Map<String, Integer>> nominalAttributeIndexMap) {
        String value = attributeEntity.getType().handle(valueExtractor);
        return attributeEntity.getType().handle(new AttributeTypeVisitor<>() {
            @Override
            public Double caseNumeric() {
                return Double.valueOf(value);
            }

            @Override
            public Double caseNominal() {
                var valuesMap = nominalAttributeIndexMap.get(attributeEntity.getAttributeName());
                if (valuesMap == null) {
                    throw new IllegalStateException(String.format("Can't find attribute [%s] values index map",
                            attributeEntity.getAttributeName()));
                }
                int index = valuesMap.entrySet()
                        .stream()
                        .filter(entry -> entry.getKey().equals(value))
                        .map(Map.Entry::getValue)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException(
                                String.format("Can't find attribute [%s] index for value [%s]",
                                        attributeEntity.getAttributeName(), value)));
                return (double) index;
            }

            @Override
            public Double caseDate() {
                LocalDateTime dateTime = LocalDateTime.parse(value, dateTimeFormatter);
                return (double) Timestamp.valueOf(dateTime).getTime();
            }
        });
    }

    private ArrayList<AttributeModel> createAttributes() {
        return IntStream.range(0, instancesEntity.getNumAttributes())
                .mapToObj(i -> {
                    var attributeEntity = attributeEntities.get(i);
                    return createAttribute(attributeEntity);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private AttributeModel createAttribute(AttributeEntity attributeEntity) {
        return attributeEntity.getType().handle(new AttributeTypeVisitor<AttributeModel>() {
            @Override
            public AttributeModel caseNumeric() {
                AttributeModel attributeModel = createAttributeModel();
                attributeModel.setType(AttributeType.NUMERIC);
                return attributeModel;
            }

            @Override
            public AttributeModel caseNominal() {
                AttributeModel attributeModel = createAttributeModel();
                attributeModel.setType(AttributeType.NOMINAL);
                var values = attributeEntity.getValues()
                        .stream()
                        .map(AttributeValueEntity::getValue)
                        .collect(Collectors.toList());
                attributeModel.setValues(values);
                return attributeModel;
            }

            @Override
            public AttributeModel caseDate() {
                AttributeModel attributeModel = createAttributeModel();
                attributeModel.setType(AttributeType.DATE);
                attributeModel.setDateFormat(dateFormat);
                return attributeModel;
            }

            AttributeModel createAttributeModel() {
                AttributeModel attributeModel = new AttributeModel();
                attributeModel.setName(attributeEntity.getAttributeName());
                return attributeModel;
            }
        });
    }
}
