package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.entity.AttributeTypeVisitor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;

import static com.ecaservice.data.storage.util.SqlUtils.getDoubleValue;
import static com.ecaservice.data.storage.util.SqlUtils.getLocalDateTimeValue;
import static com.ecaservice.data.storage.util.SqlUtils.getStringValueSafe;

/**
 * Attribute value extractor.
 *
 * @author Roman Batygin
 */
@Data
@RequiredArgsConstructor
public class AttributeValueExtractor implements AttributeTypeVisitor<String> {

    private final ResultSet resultSet;
    private final DateTimeFormatter dateTimeFormatter;
    private int columnIndex;

    @Override
    @SneakyThrows
    public String caseNumeric() {
        double val = getDoubleValue(resultSet, columnIndex);
        return String.valueOf(val);
    }

    @Override
    @SneakyThrows
    public String caseNominal() {
        return getStringValueSafe(resultSet, columnIndex);
    }

    @Override
    @SneakyThrows
    public String caseDate() {
        var val = getLocalDateTimeValue(resultSet, columnIndex);
        return val.format(dateTimeFormatter);
    }
}
