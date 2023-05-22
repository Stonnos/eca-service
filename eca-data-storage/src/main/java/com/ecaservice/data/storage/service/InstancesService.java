package com.ecaservice.data.storage.service;

import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.ClassAttributeNotSelectedException;
import com.ecaservice.data.storage.exception.SelectedAttributesOutOfBoundsException;
import com.ecaservice.data.storage.model.InstancesBatchOptions;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.db.SqlQueryHelper;
import eca.data.db.SqlTypeUtils;
import eca.data.file.model.InstancesModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import weka.core.Instances;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.ecaservice.data.storage.util.Utils.MIN_NUM_SELECTED_ATTRIBUTES;
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
    private static final String RENAME_TABLE_QUERY_FORMAT = "ALTER TABLE IF EXISTS %s RENAME TO %s";

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
    public void saveInstances(InstancesEntity instancesEntity, Instances instances) {
        log.info("Starting to save instances [{}] into table [{}].", instances.relationName(),
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
                instances.relationName(), instancesEntity.getTableName());
    }

    /**
     * Deletes specified table from database.
     *
     * @param tableName - table name
     */
    public void deleteInstances(String tableName) {
        log.info("Starting to delete table with name [{}]", tableName);
        jdbcTemplate.execute(String.format(DROP_TABLE_QUERY_FORMAT, tableName));
        log.info("Table [{}] has been deleted", tableName);
    }

    /**
     * Renames specified table.
     *
     * @param tableName    - table name
     * @param newTableName - new table name
     */
    public void renameInstances(String tableName, String newTableName) {
        log.info("Starting to rename table [{}] with new name [{}]", tableName, newTableName);
        jdbcTemplate.execute(String.format(RENAME_TABLE_QUERY_FORMAT, tableName, newTableName));
        log.info("Table [{}] has been renamed to [{}]", tableName, newTableName);
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
        log.info("Starting to get instances for table [{}], page request [{}]", tableName, pageRequestDto);
        var attributes = attributeService.getAttributes(instancesEntity);
        var sqlPreparedQuery = searchQueryCreator.buildSqlQuery(instancesEntity, pageRequestDto);
        var extractor = new DataListResultSetExtractor(instancesEntity, attributes);
        extractor.setDateTimeFormatter(DateTimeFormatter.ofPattern(ecaDsConfig.getDateFormat()));
        var dataList = jdbcTemplate.query(sqlPreparedQuery.getQuery(), sqlPreparedQuery.getArgs(), extractor);
        Long totalElements =
                jdbcTemplate.queryForObject(sqlPreparedQuery.getCountQuery(), sqlPreparedQuery.getArgs(), Long.class);
        Assert.notNull(totalElements, String.format("Expected not null total elements for table [%s]", tableName));
        log.info("Instances has been fetched for table [{}], page request [{}]", tableName, pageRequestDto);
        return PageDto.of(dataList, pageRequestDto.getPage(), totalElements);
    }

    /**
     * Gets instances from specified instances table.
     *
     * @param instancesEntity - instances entity
     * @return instances object
     */
    public Instances getInstances(InstancesEntity instancesEntity) {
        log.info("Starting to get instances from table [{}]", instancesEntity.getTableName());
        var attributes = attributeService.getAttributes(instancesEntity);
        var extractor = new InstancesResultSetExtractor(instancesEntity, attributes);
        extractor.setDateFormat(ecaDsConfig.getDateFormat());
        String query = buildSqlSelectQuery(instancesEntity.getTableName(), attributes);
        var instances = jdbcTemplate.query(query, extractor);
        log.info("Instances has been fetched from table [{}]", instancesEntity.getTableName());
        return instances;
    }

    /**
     * Gets valid instances model with selected attributes and assigned class attribute.
     * Valid instances is:
     * 1. Selected attributes number is greater than or equal to 2
     * 2. Class attribute is selected
     *
     * @param instancesEntity - instances entity
     * @return instances model object
     */
    public InstancesModel getValidInstancesModel(InstancesEntity instancesEntity) {
        log.info("Starting to get instances model with selected attributes from table [{}]",
                instancesEntity.getTableName());
        var attributes = attributeService.getSelectedAttributes(instancesEntity);
        if (attributes.size() < MIN_NUM_SELECTED_ATTRIBUTES) {
            throw new SelectedAttributesOutOfBoundsException(instancesEntity.getId());
        }
        if (instancesEntity.getClassAttribute() == null) {
            throw new ClassAttributeNotSelectedException(instancesEntity.getId());
        }
        var extractor = new InstancesModelResultSetExtractor(instancesEntity, attributes);
        extractor.setDateFormat(ecaDsConfig.getDateFormat());
        extractor.setDateTimeFormatter(DateTimeFormatter.ofPattern(ecaDsConfig.getDateFormat()));
        String query = buildSqlSelectQuery(instancesEntity.getTableName(), attributes);
        var instancesModel = jdbcTemplate.query(query, extractor);
        log.info("Instances model has been fetched for table [{}]", instancesEntity.getTableName());
        return instancesModel;
    }

    private SqlQueryHelper initializeSqlQueryHelper(InstancesEntity instancesEntity) {
        var sqlQueryHelper = new SqlQueryHelper();
        sqlQueryHelper.setDateColumnType(SqlTypeUtils.TIMESTAMP_TYPE);
        sqlQueryHelper.setUsePrimaryKeyColumn(true);
        sqlQueryHelper.setPrimaryKeyColumnName(instancesEntity.getIdColumnName());
        return sqlQueryHelper;
    }
}
