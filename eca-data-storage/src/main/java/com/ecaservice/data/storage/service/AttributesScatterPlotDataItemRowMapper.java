package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.web.dto.model.AttributesScatterPlotDataItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.ecaservice.data.storage.util.Utils.getAttributeValueByCode;

/**
 * Attributes scatter plot data item row mapper.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class AttributesScatterPlotDataItemRowMapper implements RowMapper<AttributesScatterPlotDataItemDto> {

    private static final int SCALE = 4;
    private static final int X_COLUMN_INDEX = 1;
    private static final int Y_COLUMN_INDEX = 2;
    private static final int CLASS_COLUMN_INDEX = 3;

    private final AttributeEntity xAttributeEntity;
    private final AttributeEntity yAttributeEntity;

    @Override
    public AttributesScatterPlotDataItemDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        AttributesScatterPlotDataItemDto dataItemDto = new AttributesScatterPlotDataItemDto();
        if (AttributeType.NOMINAL.equals(xAttributeEntity.getType())) {
            String xValue = resultSet.getString(X_COLUMN_INDEX);
            var attributeValue = getAttributeValueByCode(xAttributeEntity, xValue);
            dataItemDto.setXLabel(xValue);
            dataItemDto.setXValue(BigDecimal.valueOf(attributeValue.getIndex()));
        } else {
            BigDecimal xValue = resultSet.getBigDecimal(X_COLUMN_INDEX);
            dataItemDto.setXValue(xValue.setScale(SCALE, RoundingMode.HALF_UP));
        }
        if (AttributeType.NOMINAL.equals(yAttributeEntity.getType())) {
            String yValue = resultSet.getString(Y_COLUMN_INDEX);
            var attributeValue = getAttributeValueByCode(yAttributeEntity, yValue);
            dataItemDto.setYLabel(yValue);
            dataItemDto.setYValue(BigDecimal.valueOf(attributeValue.getIndex()));
        } else {
            BigDecimal yValue = resultSet.getBigDecimal(Y_COLUMN_INDEX);
            dataItemDto.setYValue(yValue.setScale(SCALE, RoundingMode.HALF_UP));
        }
        dataItemDto.setClassValue(resultSet.getString(CLASS_COLUMN_INDEX));
        return dataItemDto;
    }
}
