package com.ecaservice.data.storage.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.common.web.exception.InvalidOperationException;
import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.mapping.AttributeMapper;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.web.dto.model.ContingencyTableReportDto;
import com.ecaservice.web.dto.model.ContingencyTableRequestDto;
import eca.statistics.Statistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import weka.core.ContingencyTables;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.data.storage.util.Utils.buildNullCheckSqlSelectQuery;
import static com.ecaservice.data.storage.util.Utils.createNominalAttributeIndexMap;
import static com.ecaservice.data.storage.util.Utils.toDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContingencyTableReportService {

    private static final int X_ATTR_COLUMN_INDEX = 1;
    private static final int Y_ATTR_COLUMN_INDEX = 2;

    private final AttributeService attributeService;
    private final JdbcTemplate jdbcTemplate;
    private final AttributeMapper attributeMapper;
    private final InstancesRepository instancesRepository;

    /**
     * Calculates contingency table report.
     *
     * @param requestDto - request data
     * @return contingency table report
     */
    public ContingencyTableReportDto calculateReport(ContingencyTableRequestDto requestDto) {
        log.info("Gets contingency table for instances [{}], x attribute id [{}], y attribute id [{}]",
                requestDto.getInstancesId(), requestDto.getXAttributeId(), requestDto.getYAttributeId());
        InstancesEntity instancesEntity = instancesRepository.findById(requestDto.getInstancesId())
                .orElseThrow(() -> new EntityNotFoundException(InstancesEntity.class, requestDto.getInstancesId()));
        AttributeEntity xAttributeEntity = attributeService.getById(requestDto.getXAttributeId());
        AttributeEntity yAttributeEntity = attributeService.getById(requestDto.getYAttributeId());
        if (!xAttributeEntity.getInstancesEntity().getId().equals(instancesEntity.getId())) {
            throw new InvalidOperationException(
                    String.format("Attribute [%d] not belongs to instances [%d]", requestDto.getXAttributeId(),
                            requestDto.getInstancesId())
            );
        }
        if (!yAttributeEntity.getInstancesEntity().getId().equals(instancesEntity.getId())) {
            throw new InvalidOperationException(
                    String.format("Attribute [%d] not belongs to instances [%d]", requestDto.getYAttributeId(),
                            requestDto.getInstancesId())
            );
        }
        if (!AttributeType.NOMINAL.equals(xAttributeEntity.getType())) {
            throw new InvalidOperationException(
                    String.format("X attribute [%d] must be nominal", requestDto.getXAttributeId()));
        }
        if (!AttributeType.NOMINAL.equals(yAttributeEntity.getType())) {
            throw new InvalidOperationException(
                    String.format("Y attribute [%d] must be nominal", requestDto.getYAttributeId()));
        }
        List<List<String>> dataItems = getDataItems(instancesEntity, xAttributeEntity, yAttributeEntity);
        double[][] contingencyTable = calculateContingencyTable(dataItems, xAttributeEntity, yAttributeEntity);

        double chiVal = ContingencyTables.chiVal(contingencyTable, requestDto.isUseYates());
        int df = (xAttributeEntity.getValues().size() - 1) * (yAttributeEntity.getValues().size() - 1);
        double chiSquaredCriticalValue =
                Statistics.chiSquaredCriticalValue(requestDto.getAlphaValue().doubleValue(), df);
        var tableData = Arrays.stream(contingencyTable)
                .map(innerArray -> Arrays.stream(innerArray)
                        .boxed()
                        .map(Double::intValue)
                        .collect(Collectors.toList())
                )
                .toList();

        ContingencyTableReportDto reportDto = new ContingencyTableReportDto();
        reportDto.setXAttribute(attributeMapper.map(xAttributeEntity));
        reportDto.setYAttribute(attributeMapper.map(yAttributeEntity));
        reportDto.setChiSquaredValue(toDecimal(chiVal));
        reportDto.setDf(df);
        reportDto.setChiSquaredCriticalValue(toDecimal(chiSquaredCriticalValue));
        reportDto.setAlpha(requestDto.getAlphaValue());
        reportDto.setSignificant(chiVal > chiSquaredCriticalValue);
        reportDto.setTableData(tableData);
        log.info("Contingency table has been build for instances [{}], x attribute id [{}], y attribute id [{}]",
                requestDto.getInstancesId(), requestDto.getXAttributeId(), requestDto.getYAttributeId());
        return reportDto;
    }

    private double[][] calculateContingencyTable(List<List<String>> dataItems,
                                                 AttributeEntity xAttributeEntity,
                                                 AttributeEntity yAttributeEntity) {
        var xAttrIndexMap = createNominalAttributeIndexMap(xAttributeEntity);
        var yAttrIndexMap = createNominalAttributeIndexMap(yAttributeEntity);
        int xAttrNumValues = xAttributeEntity.getValues().size();
        int yAttrNumValues = yAttributeEntity.getValues().size();
        double[][] contingencyTable = new double[xAttrNumValues + 1][yAttrNumValues + 1];
        dataItems.forEach(row -> {
            String xVal = row.getFirst();
            String yVal = row.getLast();
            int xIndex = xAttrIndexMap.get(xVal);
            int yIndex = yAttrIndexMap.get(yVal);
            contingencyTable[xIndex][yIndex]++;
            contingencyTable[xIndex][yAttrNumValues]++;
            contingencyTable[xAttrNumValues][yIndex]++;
            contingencyTable[xAttrNumValues][yAttrNumValues]++;
        });
        return contingencyTable;
    }

    private List<List<String>> getDataItems(InstancesEntity instancesEntity,
                                            AttributeEntity xAttributeEntity,
                                            AttributeEntity yAttributeEntity) {
        List<String> columns = List.of(xAttributeEntity.getColumnName(), yAttributeEntity.getColumnName());
        String sqlQuery = buildNullCheckSqlSelectQuery(instancesEntity, columns);
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                List.of(rs.getString(X_ATTR_COLUMN_INDEX), rs.getString(Y_ATTR_COLUMN_INDEX))
        );
    }
}
