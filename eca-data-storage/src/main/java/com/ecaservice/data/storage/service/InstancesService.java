package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.ClassAttributeNotSelectedException;
import com.ecaservice.data.storage.exception.ClassAttributeValuesIsTooLowException;
import com.ecaservice.data.storage.exception.SelectedAttributesNumberIsTooLowException;
import com.ecaservice.data.storage.model.AttributeInfo;
import com.ecaservice.data.storage.model.InstancesBatchOptions;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.db.SqlQueryHelper;
import eca.data.db.SqlTypeUtils;
import eca.data.file.model.InstancesModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import weka.core.Instances;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.data.storage.util.Utils.MIN_NUM_CLASSES;
import static com.ecaservice.data.storage.util.Utils.MIN_NUM_SELECTED_ATTRIBUTES;
import static com.ecaservice.data.storage.util.Utils.buildSqlCountUniqueValuesQuery;
import static com.ecaservice.data.storage.util.Utils.buildSqlSelectQuery;

/**
 * Instances service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesService {

    private static final String DROP_TABLE_QUERY_FORMAT = "DROP TABLE IF EXISTS %s";

    private final JdbcTemplate jdbcTemplate;
    private final InstancesBatchService instancesBatchService;
    private final EcaDsConfig ecaDsConfig;
    private final SearchQueryCreator searchQueryCreator;
    private final AttributeService attributeService;

    /**
     * Saves training data into database.
     *
     * @param instancesEntity - instances entity
     * @param instances       - training data
     */
    @NewSpan
    public void saveInstances(InstancesEntity instancesEntity, Instances instances) {
        log.info("Starting to save instances [{}] into table [{}].", instancesEntity.getRelationName(),
                instancesEntity.getTableName());
        log.info("Starting to create table [{}].", instancesEntity.getTableName());
        var sqlQueryHelper = initializeSqlQueryHelper(instancesEntity);
        String createTableQuery = sqlQueryHelper.buildCreateTableQuery(instancesEntity.getTableName(), instances);
        log.trace("create table query: {}", createTableQuery);
        jdbcTemplate.execute(createTableQuery);
        log.info("Table [{}] has been successfully created.", instancesEntity.getTableName());

        log.info("Starting to save data into table [{}].", instancesEntity.getTableName());
        int batchSize = ecaDsConfig.getBatchSize();
        for (int offset = 0; offset < instances.numInstances(); offset += batchSize) {
            log.trace("Starting to save batch with limit = {}, offset = {} into table [{}].", batchSize, offset,
                    instancesEntity.getTableName());
            var batchOptions = new InstancesBatchOptions(instancesEntity.getTableName(), instances, batchSize, offset,
                    sqlQueryHelper);
            instancesBatchService.saveBatch(batchOptions);
            log.trace("{} rows has been saved into table [{}].", offset + batchSize, instancesEntity.getTableName());
        }
        log.info("Data has been saved into table [{}].", instancesEntity.getTableName());
        log.info("Data saving has been successfully completed. Instances [{}] has been saved into table [{}].",
                instancesEntity.getRelationName(), instancesEntity.getTableName());
    }

    /**
     * Deletes specified table from database.
     *
     * @param tableName - table name
     */
    @NewSpan
    public void deleteInstances(String tableName) {
        log.info("Starting to delete table with name [{}]", tableName);
        jdbcTemplate.execute(String.format(DROP_TABLE_QUERY_FORMAT, tableName));
        log.info("Table [{}] has been deleted", tableName);
    }

    /**
     * Gets instances page with specified page request params.
     *
     * @param instancesEntity - instances entity
     * @param pageRequestDto  - page request
     * @return instances page
     */
    public PageDto<List<String>> getInstances(InstancesEntity instancesEntity, PageRequestDto pageRequestDto) {
        String tableName = instancesEntity.getTableName();
        log.info("Starting to get instances [{}] for table [{}], page request [{}]", instancesEntity.getRelationName(),
                tableName, pageRequestDto);
        var attributes = attributeService.getsAttributesInfo(instancesEntity);
        var sqlPreparedQuery = searchQueryCreator.buildSqlQuery(instancesEntity, pageRequestDto);
        var extractor = new DataListResultSetExtractor(instancesEntity, attributes);
        extractor.setDateTimeFormatter(DateTimeFormatter.ofPattern(ecaDsConfig.getDateFormat()));
        var dataList = jdbcTemplate.query(sqlPreparedQuery.getQuery(), extractor, sqlPreparedQuery.getArgs());
        Long totalElements =
                jdbcTemplate.queryForObject(sqlPreparedQuery.getCountQuery(), Long.class, sqlPreparedQuery.getArgs());
        Assert.notNull(totalElements, String.format("Expected not null total elements for table [%s]", tableName));
        log.info("Instances [{}] has been fetched for table [{}], page request [{}]", instancesEntity.getRelationName(),
                tableName, pageRequestDto);
        return PageDto.of(dataList, pageRequestDto.getPage(), totalElements);
    }

    /**
     * Gets instances from specified instances table.
     *
     * @param instancesEntity - instances entity
     * @return instances object
     */
    public Instances getInstances(InstancesEntity instancesEntity) {
        log.info("Starting to get instances [{}] from table [{}]", instancesEntity.getRelationName(),
                instancesEntity.getTableName());
        var attributes = attributeService.getsAttributesInfo(instancesEntity);
        var columns = attributes.stream()
                .map(AttributeInfo::getColumnName)
                .collect(Collectors.toList());
        var extractor = new InstancesResultSetExtractor(instancesEntity, attributes);
        extractor.setDateFormat(ecaDsConfig.getDateFormat());
        String query = buildSqlSelectQuery(instancesEntity, columns);
        var instances = jdbcTemplate.query(query, extractor);
        log.info("Instances [{}] has been fetched from table [{}]", instancesEntity.getRelationName(),
                instancesEntity.getTableName());
        return instances;
    }

    /**
     * Gets valid instances model with selected attributes and assigned class attribute.
     * Valid instances is:
     * 1. Selected attributes number is greater than or equal to 2
     * 2. Class attribute is selected
     * 3. Class values number in table is greater than or equal to 2
     *
     * @param instancesEntity - instances entity
     * @return instances model object
     */
    @NewSpan
    public InstancesModel getValidInstancesModel(InstancesEntity instancesEntity) {
        log.info("Starting to get valid instances [{}] model from table [{}]", instancesEntity.getRelationName(),
                instancesEntity.getTableName());
        var attributes = attributeService.getSelectedAttributes(instancesEntity);
        if (attributes.size() < MIN_NUM_SELECTED_ATTRIBUTES) {
            throw new SelectedAttributesNumberIsTooLowException(instancesEntity.getId());
        }
        if (instancesEntity.getClassAttribute() == null || !instancesEntity.getClassAttribute().isSelected()) {
            throw new ClassAttributeNotSelectedException(instancesEntity.getId());
        }
        int countUniqueClassesInTable = countUniqueClassValuesInTable(instancesEntity);
        if (countUniqueClassesInTable < MIN_NUM_CLASSES) {
            throw new ClassAttributeValuesIsTooLowException(instancesEntity.getId());
        }
        var columns = attributes.stream()
                .map(AttributeEntity::getColumnName)
                .collect(Collectors.toList());
        String query = buildSqlSelectQuery(instancesEntity, columns);
        var extractor = new InstancesModelResultSetExtractor(instancesEntity, attributes);
        extractor.setDateFormat(ecaDsConfig.getDateFormat());
        var instancesModel = jdbcTemplate.query(query, extractor);
        log.info("Instances [{}] model has been fetched for table [{}]", instancesEntity.getRelationName(),
                instancesEntity.getTableName());
        return instancesModel;
    }

    private int countUniqueClassValuesInTable(InstancesEntity instancesEntity) {
        String query = buildSqlCountUniqueValuesQuery(instancesEntity.getTableName(),
                instancesEntity.getClassAttribute().getColumnName());
        var resultValue = jdbcTemplate.queryForObject(query, Integer.class);
        Assert.notNull(resultValue, String.format("Expected not null count unique value for table [%s], column [%s]",
                instancesEntity.getTableName(), instancesEntity.getClassAttribute().getColumnName()));
        return resultValue;
    }

    private SqlQueryHelper initializeSqlQueryHelper(InstancesEntity instancesEntity) {
        var sqlQueryHelper = new SqlQueryHelper();
        sqlQueryHelper.setDateColumnType(SqlTypeUtils.TIMESTAMP_TYPE);
        sqlQueryHelper.setUsePrimaryKeyColumn(true);
        sqlQueryHelper.setPrimaryKeyColumnName(instancesEntity.getIdColumnName());
        return sqlQueryHelper;
    }
}
