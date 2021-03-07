package com.ecaservice.data.storage.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.data.storage.config.EcaDsConfig;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.TableExistsException;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.core.Instances;

import java.time.LocalDateTime;

import static com.ecaservice.data.storage.entity.InstancesEntity_.CREATED;
import static com.ecaservice.data.storage.util.FilterUtils.buildSort;
import static com.ecaservice.data.storage.util.FilterUtils.buildSpecification;

/**
 * Service for saving data file into database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private static final String TABLE_NOT_EXISTS_QUERY_FORMAT =
            "SELECT NOT EXISTS (SELECT FROM pg_tables WHERE schemaname = 'public' AND tablename = '%s')";

    private final EcaDsConfig ecaDsConfig;
    private final InstancesService instancesService;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;
    private final InstancesRepository instancesRepository;

    @Override
    public Page<InstancesEntity> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATED, pageRequestDto.isAscending());
        Specification<InstancesEntity> specification = buildSpecification(pageRequestDto);
        int pageSize = Integer.min(pageRequestDto.getSize(), ecaDsConfig.getMaxPageSize());
        return instancesRepository.findAll(specification, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }

    @Override
    public InstancesEntity saveData(Instances instances, String tableName) {
        log.info("Starting to save instances into table [{}]", tableName);
        instancesService.saveInstances(tableName, instances);
        InstancesEntity instancesEntity =  saveInstancesEntity(tableName, instances);
        log.info("Instances has been saved into table [{}]", tableName);
        return instancesEntity;
    }

    @Override
    @Transactional
    public void deleteData(long id) {
        log.info("Starting to delete instances with id [{}]", id);
        InstancesEntity instancesEntity = getById(id);
        instancesService.deleteInstances(instancesEntity.getTableName());
        instancesRepository.deleteById(id);
        log.info("Instances [{}] has been deleted", id);
    }

    @Override
    @Transactional
    public void renameData(long id, String newName) {
        log.info("Starting to rename instances [{}] with new name [{}]", id, newName);
        if (tableExists(newName)) {
            throw new TableExistsException(newName);
        }
        InstancesEntity instancesEntity = getById(id);
        if (!instancesEntity.getTableName().equals(newName)) {
            instancesService.renameInstances(instancesEntity.getTableName(), newName);
            instancesEntity.setTableName(newName);
            instancesRepository.save(instancesEntity);
            log.info("Instances [{}] has been renamed to [{}]", id, newName);
        }
    }

    private InstancesEntity getById(long id) {
        return instancesRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(InstancesEntity.class, id));
    }

    private InstancesEntity saveInstancesEntity(String tableName, Instances instances) {
        InstancesEntity instancesEntity = new InstancesEntity();
        instancesEntity.setTableName(tableName);
        instancesEntity.setNumAttributes(instances.numAttributes());
        instancesEntity.setNumInstances(instances.numInstances());
        instancesEntity.setCreatedBy(userService.getCurrentUser().getUsername());
        instancesEntity.setCreated(LocalDateTime.now());
        return instancesRepository.save(instancesEntity);
    }

    private boolean tableExists(String tableName) {
        Boolean result =
                jdbcTemplate.queryForObject(String.format(TABLE_NOT_EXISTS_QUERY_FORMAT, tableName), Boolean.class);
        return Boolean.TRUE.equals(result);
    }
}
