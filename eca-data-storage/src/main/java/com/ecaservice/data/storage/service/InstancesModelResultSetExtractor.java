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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        instancesModel.setRelationName(instancesEntity.getTableName());
        instancesModel.setAttributes(attributes);
        var valueExtractor = new AttributeValueExtractor(resultSet, DateTimeFormatter.ofPattern(dateFormat));
        while (resultSet.next()) {
            InstanceModel instance = new InstanceModel();
            List<String> values = newArrayList();
            for (int i = 1; i <= attributeEntities.size(); i++) {
                AttributeEntity attributeEntity = attributeEntities.get(i - 1);
                if (resultSet.getObject(i) == null) {
                    values.add(null);
                } else {
                    valueExtractor.setColumnIndex(i);
                    String value = attributeEntity.getType().handle(valueExtractor);
                    values.add(value);
                }
            }
            instance.setValues(values);
            instancesModel.getInstances().add(instance);
        }
        if (instancesEntity.getClassAttribute() != null) {
            instancesModel.setClassName(instancesEntity.getClassAttribute().getColumnName());
        }
        return instancesModel;
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
                attributeModel.setName(attributeEntity.getColumnName());
                return attributeModel;
            }
        });
    }
}
