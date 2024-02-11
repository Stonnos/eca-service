package com.ecaservice.data.storage.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.AttributeTypeVisitor;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.mapping.InstancesMapper;
import com.ecaservice.data.storage.model.AttributeInfo;
import com.ecaservice.data.storage.model.FrequencyDiagramModel;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.web.dto.model.AttributeStatisticsDto;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.FrequencyDiagramDataDto;
import com.ecaservice.web.dto.model.InstancesStatisticsDto;
import eca.util.FrequencyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.data.storage.util.StatisticsUtils.contains;
import static com.ecaservice.data.storage.util.StatisticsUtils.max;
import static com.ecaservice.data.storage.util.StatisticsUtils.mean;
import static com.ecaservice.data.storage.util.StatisticsUtils.min;
import static com.ecaservice.data.storage.util.StatisticsUtils.variance;
import static com.ecaservice.data.storage.util.Utils.getAttributeValueByCode;
import static com.ecaservice.data.storage.util.Utils.toDecimal;

/**
 * Instances statistics service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesStatisticsService {

    private static final String GET_ATTRIBUTE_VALUES_SQL_QUERY_FORMAT = "select {0} from {1} where {0} is not null";

    private static final String GET_NOMINAL_ATTRIBUTE_VALUES_STATS_SQL_QUERY_FORMAT =
            "select {0}, count(*) from {1} where {0} is not null group by {0}";
    private static final int SCALE = 4;
    private static final int CODE_INDEX = 1;
    private static final int FREQUENCY_INDEX = 2;
    private static final int FIRST_COLUMN_VALUE_INDEX = 1;
    private static final long ZERO = 0L;

    private final JdbcTemplate jdbcTemplate;
    private final InstancesMapper instancesMapper;
    private final AttributeService attributeService;
    private final InstancesRepository instancesRepository;

    /**
     * Gets instances statistics.
     *
     * @param id - instances id
     * @return instances statistics
     */
    public InstancesStatisticsDto getInstancesStatistics(long id) {
        log.info("Gets instances [{}] statistics", id);
        var instancesEntity = instancesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(InstancesEntity.class, id));
        InstancesStatisticsDto instancesStatisticsDto = instancesMapper.mapToStatistics(instancesEntity);
        var attributesInfoList = attributeService.getsAttributesInfo(instancesEntity);
        setClassStatistics(instancesStatisticsDto, instancesEntity, attributesInfoList);
        var attributesCountingStats = attributesInfoList.stream()
                .collect(Collectors.groupingBy(AttributeInfo::getType, Collectors.counting()));
        instancesStatisticsDto.setNumNumericAttributes(
                attributesCountingStats.getOrDefault(AttributeType.NUMERIC, ZERO).intValue());
        instancesStatisticsDto.setNumNominalAttributes(
                attributesCountingStats.getOrDefault(AttributeType.NOMINAL, ZERO).intValue());
        instancesStatisticsDto.setNumDateAttributes(
                attributesCountingStats.getOrDefault(AttributeType.DATE, ZERO).intValue());
        log.debug("Instances [{}] statistics has been calculated: {}", instancesStatisticsDto.getId(),
                instancesStatisticsDto);
        log.info("Instances [{}] statistics has been fetched", id);
        return instancesStatisticsDto;
    }

    /**
     * Gets attribute statistics.
     *
     * @param id - attribute id
     * @return attribute statistics
     */
    public AttributeStatisticsDto getAttributeStatistics(long id) {
        log.info("Gets attribute [{}] statistics", id);
        var attributeEntity = attributeService.getById(id);
        AttributeStatisticsDto attributeStatisticsDto = new AttributeStatisticsDto();
        attributeStatisticsDto.setId(attributeEntity.getId());
        attributeStatisticsDto.setName(attributeEntity.getAttributeName());
        attributeStatisticsDto.setIndex(attributeEntity.getIndex());
        attributeStatisticsDto.setType(
                new EnumDto(attributeEntity.getType().name(), attributeEntity.getType().getDescription()));
        calculateAttributeStatistics(attributeEntity, attributeStatisticsDto);
        log.debug("Attribute [{}] statistics has been calculated: {}", attributeEntity.getId(), attributeStatisticsDto);
        log.info("Attribute [{}] statistics has been fetched", id);
        return attributeStatisticsDto;
    }

    private void calculateAttributeStatistics(AttributeEntity attributeEntity,
                                              AttributeStatisticsDto attributeStatisticsDto) {
        attributeEntity.getType().handle(new AttributeTypeVisitor<Void>() {
            @Override
            public Void caseNumeric() {
                String sqlQuery =
                        MessageFormat.format(GET_ATTRIBUTE_VALUES_SQL_QUERY_FORMAT, attributeEntity.getColumnName(),
                                attributeEntity.getInstancesEntity().getTableName());
                log.debug("Get numeric attribute [{}] values sql query: {}", attributeEntity.getId(), sqlQuery);
                List<Double> values = jdbcTemplate.query(sqlQuery,
                        (resultSet, rowNum) -> resultSet.getDouble(FIRST_COLUMN_VALUE_INDEX));
                calculateStatisticsForNumericAttribute(attributeStatisticsDto, attributeEntity, values);
                return null;
            }

            @Override
            public Void caseNominal() {
                String sqlQuery = MessageFormat.format(GET_NOMINAL_ATTRIBUTE_VALUES_STATS_SQL_QUERY_FORMAT,
                        attributeEntity.getColumnName(), attributeEntity.getInstancesEntity().getTableName());
                log.debug("Get nominal attribute [{}] values stats sql query: {}", attributeEntity.getId(), sqlQuery);
                List<FrequencyDiagramDataDto> frequencyDiagramValues = jdbcTemplate.query(sqlQuery,
                        (resultSet, rowNum) -> {
                            FrequencyDiagramDataDto frequencyDiagramDataDto = new FrequencyDiagramDataDto();
                            frequencyDiagramDataDto.setCode(resultSet.getString(CODE_INDEX));
                            frequencyDiagramDataDto.setFrequency(resultSet.getInt(FREQUENCY_INDEX));
                            return frequencyDiagramDataDto;
                        });
                sortNominalCodesByValueIndex(attributeEntity, frequencyDiagramValues);
                attributeStatisticsDto.setFrequencyDiagramValues(frequencyDiagramValues);
                return null;
            }

            @Override
            public Void caseDate() {
                String sqlQuery =
                        MessageFormat.format(GET_ATTRIBUTE_VALUES_SQL_QUERY_FORMAT, attributeEntity.getColumnName(),
                                attributeEntity.getInstancesEntity().getTableName());
                log.debug("Get date attribute [{}] values sql query: {}", attributeEntity.getId(), sqlQuery);
                List<Double> values = jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> {
                    long timestamp = resultSet.getTimestamp(FIRST_COLUMN_VALUE_INDEX).getTime();
                    return (double) timestamp;
                });
                calculateStatisticsForNumericAttribute(attributeStatisticsDto, attributeEntity, values);
                return null;
            }
        });
    }

    private void sortNominalCodesByValueIndex(AttributeEntity attributeEntity,
                                              List<FrequencyDiagramDataDto> frequencyDiagramValues) {
        frequencyDiagramValues.sort((fd1, fd2) -> {
            var fd1Value = getAttributeValueByCode(attributeEntity, fd1.getCode());
            var fd2Value = getAttributeValueByCode(attributeEntity, fd2.getCode());
            return Integer.compare(fd1Value.getIndex(), fd2Value.getIndex());
        });
    }

    private void calculateStatisticsForNumericAttribute(AttributeStatisticsDto attributeStatisticsDto,
                                                        AttributeEntity attributeEntity,
                                                        List<Double> values) {
        double minValue = min(values);
        double maxValue = max(values);
        double meanValue = mean(values);
        double varianceValue = variance(values, meanValue);
        double stdDevValue = Math.sqrt(varianceValue);

        var frequencyDiagramValues =
                calculateFrequencyDiagramDataForNumericAttribute(attributeEntity, values, minValue, maxValue);
        var frequencyDiagramDtoValues = frequencyDiagramValues.stream()
                .map(frequencyDiagramModel -> {
                    FrequencyDiagramDataDto frequencyDiagramDataDto = new FrequencyDiagramDataDto();
                    frequencyDiagramDataDto.setLowerBound(toDecimal(frequencyDiagramModel.getLowerBound(), SCALE));
                    frequencyDiagramDataDto.setUpperBound(toDecimal(frequencyDiagramModel.getUpperBound(), SCALE));
                    frequencyDiagramDataDto.setFrequency(frequencyDiagramModel.getFrequency());
                    return frequencyDiagramDataDto;
                })
                .collect(Collectors.toList());

        attributeStatisticsDto.setMinValue(toDecimal(minValue, SCALE));
        attributeStatisticsDto.setMaxValue(toDecimal(maxValue, SCALE));
        attributeStatisticsDto.setMeanValue(toDecimal(meanValue, SCALE));
        attributeStatisticsDto.setVarianceValue(toDecimal(varianceValue, SCALE));
        attributeStatisticsDto.setStdDevValue(toDecimal(stdDevValue, SCALE));
        attributeStatisticsDto.setFrequencyDiagramValues(frequencyDiagramDtoValues);
    }

    private List<FrequencyDiagramModel> calculateFrequencyDiagramDataForNumericAttribute(
            AttributeEntity attributeEntity,
            List<Double> values,
            double minValue,
            double maxValue) {
        var frequencyDiagramValues = createFrequencyDiagramIntervals(attributeEntity, minValue, maxValue);
        var firstInterval = frequencyDiagramValues.iterator().next();
        values.forEach(value -> {
            if (contains(firstInterval.getLowerBound(), firstInterval.getUpperBound(), value, true, true)) {
                firstInterval.setFrequency(firstInterval.getFrequency() + 1);
            }
            for (int i = 1; i < frequencyDiagramValues.size(); i++) {
                var interval = frequencyDiagramValues.get(i);
                if (contains(interval.getLowerBound(), interval.getUpperBound(), value, false, true)) {
                    interval.setFrequency(interval.getFrequency() + 1);
                    break;
                }
            }
        });
        return frequencyDiagramValues;
    }

    private List<FrequencyDiagramModel> createFrequencyDiagramIntervals(AttributeEntity attributeEntity,
                                                                        double minValue,
                                                                        double maxValue) {
        int numIntervals =
                FrequencyUtils.stigessFormula(attributeEntity.getInstancesEntity().getNumInstances());
        List<FrequencyDiagramModel> frequencyDiagramValues = new ArrayList<>(numIntervals);
        double delta = (maxValue - minValue) / numIntervals;
        for (int i = 1; i <= numIntervals; i++) {
            double lowerBound = minValue + (i - 1) * delta;
            double upperBound = minValue + i * delta;
            FrequencyDiagramModel frequencyDiagramDataDto = new FrequencyDiagramModel();
            frequencyDiagramDataDto.setLowerBound(lowerBound);
            frequencyDiagramDataDto.setUpperBound(upperBound);
            frequencyDiagramValues.add(frequencyDiagramDataDto);
        }
        return frequencyDiagramValues;
    }

    private void setClassStatistics(InstancesStatisticsDto instancesStatisticsDto,
                                    InstancesEntity instancesEntity,
                                    List<AttributeInfo> attributesInfoList) {
        if (instancesEntity.getClassAttribute() != null) {
            AttributeEntity classAttribute = instancesEntity.getClassAttribute();
            AttributeInfo classAttributeInfo = attributesInfoList.stream()
                    .filter(attributeInfo -> attributeInfo.getAttributeName().equals(classAttribute.getAttributeName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("Can't find class [%s] attribute info for instances [%s]",
                                    classAttribute.getAttributeName(), instancesEntity.getRelationName())));
            instancesStatisticsDto.setClassName(classAttribute.getAttributeName());
            instancesStatisticsDto.setNumClasses(classAttributeInfo.getValues().size());
        }
    }
}
