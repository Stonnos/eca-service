package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Implements instances result set extractor.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class DataListResultSetExtractor implements ResultSetExtractor<List<List<String>>> {

    private final InstancesEntity instancesEntity;
    private final List<AttributeEntity> attributeEntities;

    @Getter
    @Setter
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<List<String>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<List<String>> dataList = newArrayList();
        var valueExtractor = new AttributeValueExtractor(resultSet, dateTimeFormatter);
        while (resultSet.next()) {
            List<String> row = newArrayList();
            for (int i = 1; i <= instancesEntity.getNumAttributes(); i++) {
                var attributeEntity = attributeEntities.get(i - 1);
                //Skip id column
                int columnIndex = i + 1;
                if (resultSet.getObject(columnIndex) == null) {
                    row.add(null);
                } else {
                    valueExtractor.setColumnIndex(columnIndex);
                    String value = attributeEntity.getType().handle(valueExtractor);
                    row.add(value);
                }
            }
            dataList.add(row);
        }
        return dataList;
    }
}
