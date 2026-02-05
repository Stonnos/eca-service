package com.ecaservice.data.storage.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.common.web.exception.InvalidOperationException;
import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.ClassAttributeNotSelectedException;
import com.ecaservice.data.storage.mapping.AttributeMapper;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.web.dto.model.AttributesScatterPlotDataItemDto;
import com.ecaservice.web.dto.model.AttributesScatterPlotDataSetDto;
import com.ecaservice.web.dto.model.AttributesScatterPlotDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ecaservice.data.storage.util.Utils.COMMA_SEPARATOR;

/**
 * Attributes scatter plot service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttributesScatterPlotService {

    private static final String SELECT_QUERY_WITH_NOT_NULL_VALUES = "select %s from %s where %s";
    private static final String NOT_NULL_CHECK = "%s is not null";
    private static final String AND = " and ";

    private final AttributeService attributeService;
    private final JdbcTemplate jdbcTemplate;
    private final AttributeMapper attributeMapper;
    private final InstancesRepository instancesRepository;

    /**
     * Gets scatter plot data.
     *
     * @param instancesId  - instances id
     * @param xAttributeId - x attribute id
     * @param yAttributeId - y attribute id
     * @return scatter plot data
     */
    public AttributesScatterPlotDto getScatterPlot(Long instancesId, Long xAttributeId, Long yAttributeId) {
        log.info("Gets scatter plot for instances [{}], x attribute id [{}], y attribute id [{}]", instancesId,
                xAttributeId, yAttributeId);
        InstancesEntity instancesEntity = instancesRepository.findById(instancesId)
                .orElseThrow(() -> new EntityNotFoundException(InstancesEntity.class, instancesId));
        AttributeEntity xAttributeEntity = attributeService.getById(xAttributeId);
        AttributeEntity yAttributeEntity = attributeService.getById(yAttributeId);
        if (!xAttributeEntity.getInstancesEntity().getId().equals(instancesEntity.getId())) {
            throw new InvalidOperationException(
                    String.format("Attribute [%d] not belongs to instances [%d]", xAttributeId, instancesId));
        }
        if (!yAttributeEntity.getInstancesEntity().getId().equals(instancesEntity.getId())) {
            throw new InvalidOperationException(
                    String.format("Attribute [%d] not belongs to instances [%d]", yAttributeId, instancesId));
        }
        if (instancesEntity.getClassAttribute() == null || !instancesEntity.getClassAttribute().isSelected()) {
            throw new ClassAttributeNotSelectedException(instancesEntity.getId());
        }
        AttributesScatterPlotDto attributesScatterPlotDto = new AttributesScatterPlotDto();
        attributesScatterPlotDto.setXAxisAttribute(attributeMapper.map(xAttributeEntity));
        attributesScatterPlotDto.setYAxisAttribute(attributeMapper.map(yAttributeEntity));
        List<AttributesScatterPlotDataItemDto> dataItems =
                getDataItems(instancesEntity, xAttributeEntity, yAttributeEntity);
        var groupClassDataItems = dataItems.stream()
                .collect(Collectors.groupingBy(AttributesScatterPlotDataItemDto::getClassValue));
        List<AttributesScatterPlotDataSetDto> dataSets = groupClassDataItems.entrySet()
                .stream()
                .map(entry -> new AttributesScatterPlotDataSetDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        attributesScatterPlotDto.setDataSets(dataSets);
        log.info("Scatter plot has been fetched for instances [{}], x attribute id [{}], y attribute id [{}]",
                instancesId, xAttributeId, yAttributeId);
        return attributesScatterPlotDto;
    }

    private List<AttributesScatterPlotDataItemDto> getDataItems(InstancesEntity instancesEntity,
                                                                AttributeEntity xAttributeEntity,
                                                                AttributeEntity yAttributeEntity) {
        List<String> columns = List.of(xAttributeEntity.getColumnName(), yAttributeEntity.getColumnName(),
                instancesEntity.getClassAttribute().getColumnName());
        String sqlQuery = buildSqlSelectQuery(instancesEntity, columns);
        var rowMapper =  new AttributesScatterPlotDataItemRowMapper(xAttributeEntity, yAttributeEntity);
        return jdbcTemplate.query(sqlQuery, rowMapper);
    }

    private String buildSqlSelectQuery(InstancesEntity instancesEntity, List<String> columns) {
        String attributes = StringUtils.join(columns, COMMA_SEPARATOR);
        StringBuilder whereSubQuery = new StringBuilder();
        IntStream.range(0, columns.size() - 1).forEach(i -> {
            whereSubQuery.append(String.format(NOT_NULL_CHECK, columns.get(i)));
            whereSubQuery.append(AND);
        });
        whereSubQuery.append(String.format(NOT_NULL_CHECK, columns.getLast()));
        return String.format(SELECT_QUERY_WITH_NOT_NULL_VALUES, attributes, instancesEntity.getTableName(),
                whereSubQuery);
    }
}
