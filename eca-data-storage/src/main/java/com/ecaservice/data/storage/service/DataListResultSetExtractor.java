package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeType;
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

import static com.ecaservice.data.storage.util.SqlUtils.getStringValueSafe;
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
        while (resultSet.next()) {
            List<String> row = newArrayList();
            for (int i = 1; i <= instancesEntity.getNumAttributes(); i++) {
                var attributeEntity = attributeEntities.get(i - 1);
                if (resultSet.getObject(i) == null) {
                    row.add(null);
                } else if (AttributeType.DATE.equals(attributeEntity.getType())) {
                    var value = resultSet.getTimestamp(i).toLocalDateTime();
                    row.add(value.format(dateTimeFormatter));
                } else if (AttributeType.NUMERIC.equals(attributeEntity.getType())) {
                    double value = resultSet.getBigDecimal(i).doubleValue();
                    row.add(String.valueOf(value));
                } else {
                    row.add(getStringValueSafe(resultSet, i));
                }
            }
            dataList.add(row);
        }
        return dataList;
    }
}
