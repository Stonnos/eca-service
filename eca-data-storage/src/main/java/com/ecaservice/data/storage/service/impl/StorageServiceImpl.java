package com.ecaservice.data.storage.service.impl;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.entity.InstancesEntity_;
import com.ecaservice.data.storage.exception.EmptyDataException;
import com.ecaservice.data.storage.exception.TableExistsException;
import com.ecaservice.data.storage.filter.InstancesFilter;
import com.ecaservice.data.storage.model.ColumnModel;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.InstancesService;
import com.ecaservice.data.storage.service.StorageService;
import com.ecaservice.data.storage.service.TableMetaDataProvider;
import com.ecaservice.data.storage.service.TableNameService;
import com.ecaservice.data.storage.service.UserService;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.data.storage.config.audit.AuditCodes.DELETE_INSTANCES;
import static com.ecaservice.data.storage.config.audit.AuditCodes.RENAME_INSTANCES;
import static com.ecaservice.data.storage.config.audit.AuditCodes.SAVE_INSTANCES;
import static com.ecaservice.data.storage.entity.InstancesEntity_.CREATED;

/**
 * Service for saving data file into database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private static final List<String> INSTANCES_GLOBAL_FILTER_FIELDS = List.of(
            InstancesEntity_.TABLE_NAME,
            InstancesEntity_.CREATED_BY
    );

    private final EcaDsConfig ecaDsConfig;
    private final InstancesService instancesService;
    private final UserService userService;
    private final TableNameService tableNameService;
    private final TableMetaDataProvider tableMetaDataProvider;
    private final InstancesRepository instancesRepository;

    @Override
    public Page<InstancesEntity> getNextPage(PageRequestDto pageRequestDto) {
        log.info("Gets instances next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATED, pageRequestDto.isAscending());
        InstancesFilter filter = new InstancesFilter(pageRequestDto.getSearchQuery(), INSTANCES_GLOBAL_FILTER_FIELDS,
                pageRequestDto.getFilters());
        int pageSize = Integer.min(pageRequestDto.getSize(), ecaDsConfig.getMaxPageSize());
        var instancesPage =
                instancesRepository.findAll(filter, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
        log.info("Instances page [{} of {}] with size [{}] has been fetched for page request [{}]",
                instancesPage.getNumber(), instancesPage.getTotalPages(), instancesPage.getNumberOfElements(),
                pageRequestDto);
        return instancesPage;
    }

    @Override
    @Audit(value = SAVE_INSTANCES, correlationIdKey = "#result.id")
    public InstancesEntity saveData(Instances instances, String tableName) {
        log.info("Starting to save instances into table [{}]", tableName);
        if (tableNameService.tableExists(tableName)) {
            throw new TableExistsException(tableName);
        }
        if (instances.isEmpty()) {
            throw new EmptyDataException();
        }
        instancesService.saveInstances(tableName, instances);
        InstancesEntity instancesEntity = saveInstancesEntity(tableName, instances);
        log.info("Instances has been saved into table [{}]", tableName);
        return instancesEntity;
    }

    @Override
    @Audit(value = DELETE_INSTANCES, correlationIdKey = "#id")
    @Transactional
    public String deleteData(long id) {
        log.info("Starting to delete instances with id [{}]", id);
        InstancesEntity instancesEntity = getById(id);
        instancesService.deleteInstances(instancesEntity.getTableName());
        instancesRepository.deleteById(id);
        log.info("Instances [{}] has been deleted", id);
        return instancesEntity.getTableName();
    }

    @Override
    public PageDto<List<String>> getData(long id, PageRequestDto pageRequestDto) {
        log.info("Starting to get instances data with id [{}]", id);
        InstancesEntity instancesEntity = getById(id);
        return instancesService.getInstances(instancesEntity.getTableName(), pageRequestDto);
    }

    @Override
    public Instances getInstances(InstancesEntity instancesEntity) {
        log.info("Starting to get instances data with id [{}]", instancesEntity.getId());
        return instancesService.getInstances(instancesEntity.getTableName());
    }

    @Override
    public List<String> getAttributes(long id) {
        log.info("Gets instances [{}] attributes", id);
        InstancesEntity instancesEntity = getById(id);
        return tableMetaDataProvider.getTableColumns(instancesEntity.getTableName())
                .stream()
                .map(ColumnModel::getColumnName)
                .collect(Collectors.toList());
    }

    @Override
    @Audit(value = RENAME_INSTANCES, correlationIdKey = "#id")
    @Transactional
    public String renameData(long id, String newName) {
        log.info("Starting to rename instances [{}] with new name [{}]", id, newName);
        InstancesEntity instancesEntity = getById(id);
        String oldTableName = instancesEntity.getTableName();
        if (!instancesEntity.getTableName().equals(newName)) {
            if (tableNameService.tableExists(newName)) {
                throw new TableExistsException(newName);
            }
            instancesService.renameInstances(instancesEntity.getTableName(), newName);
            instancesEntity.setTableName(newName);
            instancesRepository.save(instancesEntity);
            log.info("Instances [{}] has been renamed to [{}]", id, newName);
        }
        log.info("Rename instances [{}] has been finished", instancesEntity.getId());
        return oldTableName;
    }

    @Override
    public InstancesEntity getById(long id) {
        return instancesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(InstancesEntity.class, id));
    }

    private InstancesEntity saveInstancesEntity(String tableName, Instances instances) {
        InstancesEntity instancesEntity = new InstancesEntity();
        instancesEntity.setTableName(tableName);
        instancesEntity.setNumAttributes(instances.numAttributes());
        instancesEntity.setNumInstances(instances.numInstances());
        instancesEntity.setCreatedBy(userService.getCurrentUser());
        instancesEntity.setCreated(LocalDateTime.now());
        return instancesRepository.save(instancesEntity);
    }
}
