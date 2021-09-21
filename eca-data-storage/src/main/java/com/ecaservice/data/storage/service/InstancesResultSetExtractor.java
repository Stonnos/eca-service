package com.ecaservice.data.storage.service;

import eca.data.db.InstancesExtractor;
import eca.data.db.InstancesResultSetConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import weka.core.Instances;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implements instances result set extractor.
 *
 * @author Roman Batygin
 */
@Component
@RequiredArgsConstructor
public class InstancesResultSetExtractor implements ResultSetExtractor<Instances> {

    private final InstancesExtractor instancesExtractor;
    private final InstancesResultSetConverter instancesResultSetConverter;

    @Override
    public Instances extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        var instancesResultSet = instancesExtractor.extractData(resultSet);
        return instancesResultSetConverter.convert(instancesResultSet);
    }
}
