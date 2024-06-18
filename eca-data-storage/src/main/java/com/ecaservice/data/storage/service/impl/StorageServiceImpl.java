package com.ecaservice.data.storage.service.impl;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeType;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.exception.ClassAttributeValuesIsTooLowException;
import com.ecaservice.data.storage.exception.EmptyDataException;
import com.ecaservice.data.storage.exception.InstancesExistsException;
import com.ecaservice.data.storage.exception.InvalidClassAttributeTypeException;
import com.ecaservice.data.storage.filter.InstancesFilter;
import com.ecaservice.data.storage.mapping.AttributeMapper;
import com.ecaservice.data.storage.repository.AttributeRepository;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.AttributeService;
import com.ecaservice.data.storage.service.InstancesService;
import com.ecaservice.data.storage.service.InstancesTransformer;
import com.ecaservice.data.storage.service.StorageService;
import com.ecaservice.data.storage.service.UserService;
import com.ecaservice.web.dto.model.AttributeDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import eca.data.file.model.InstancesModel;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.data.storage.config.audit.AuditCodes.DELETE_INSTANCES;
import static com.ecaservice.data.storage.config.audit.AuditCodes.RENAME_INSTANCES;
import static com.ecaservice.data.storage.config.audit.AuditCodes.SAVE_INSTANCES;
import static com.ecaservice.data.storage.config.audit.AuditCodes.SELECT_ALL_ATTRIBUTES;
import static com.ecaservice.data.storage.config.audit.AuditCodes.SET_CLASS_ATTRIBUTE;
import static com.ecaservice.data.storage.dictionary.FilterDictionaries.INSTANCES_TEMPLATE;
import static com.ecaservice.data.storage.entity.InstancesEntity_.CREATED;
import static com.ecaservice.data.storage.util.Utils.MIN_NUM_CLASSES;

/**
 * Service for saving data file into database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service("storageServiceImpl")
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private static final String ID_COLUMN_NAME_FORMAT = "id_%d";
    private static final String TABLE_NAME_FORMAT = "data_set_%s";

    private final FilterTemplateService filterTemplateService;
    private final InstancesService instancesService;
    private final AttributeService attributeService;
    private final UserService userService;

    private final InstancesTransformer instancesTransformer;
    private final AttributeMapper attributeMapper;
    private final InstancesRepository instancesRepository;
    private final AttributeRepository attributeRepository;

    @Override
    public Page<InstancesEntity> getNextPage(PageRequestDto pageRequestDto) {
        log.info("Gets instances next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortFields(), CREATED, true);
        var globalFilterFields = filterTemplateService.getGlobalFilterFields(INSTANCES_TEMPLATE);
        var filter =
                new InstancesFilter(pageRequestDto.getSearchQuery(), globalFilterFields, pageRequestDto.getFilters());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var instancesPage = instancesRepository.findAll(filter, pageRequest);
        log.info("Instances page [{} of {}] with size [{}] has been fetched for page request [{}]",
                instancesPage.getNumber(), instancesPage.getTotalPages(), instancesPage.getNumberOfElements(),
                pageRequestDto);
        return instancesPage;
    }

    @Override
    @NewSpan
    @Audit(value = SAVE_INSTANCES, correlationIdKey = "#result.id")
    @Locked(lockName = "saveOrUpdateDataSet", key = "#relationName")
    @Transactional
    public InstancesEntity saveData(Instances instances, String relationName) {
        log.info("Starting to save instances with relation name [{}]", relationName);
        if (instancesRepository.existsByRelationName(relationName)) {
            throw new InstancesExistsException(relationName);
        }
        if (instances.isEmpty()) {
            throw new EmptyDataException();
        }
        var transformedInstances = instancesTransformer.transform(instances);
        var instancesEntity = saveInstancesEntity(relationName, transformedInstances);
        //Gets original attribute names
        List<String> attributeNames = IntStream.range(0, instances.numAttributes())
                .mapToObj(i -> instances.attribute(i).name())
                .collect(Collectors.toList());
        var attributes = attributeService.saveAttributes(instancesEntity, transformedInstances, attributeNames);
        setClassAttribute(transformedInstances, instancesEntity, attributes);
        instancesService.saveInstances(instancesEntity, transformedInstances);
        log.info("Instances [{}] has been saved", relationName);
        return instancesEntity;
    }

    @Override
    @NewSpan
    @Audit(value = DELETE_INSTANCES, correlationIdKey = "#id")
    @Transactional
    public String deleteData(long id) {
        log.info("Starting to delete instances with id [{}]", id);
        InstancesEntity instancesEntity = getById(id);
        instancesService.deleteInstances(instancesEntity.getTableName());
        unsetClassAttribute(instancesEntity);
        attributeService.deleteAttributes(instancesEntity);
        instancesRepository.deleteById(id);
        log.info("Instances [{}] with id [{}] has been deleted", instancesEntity.getRelationName(), id);
        return instancesEntity.getRelationName();
    }

    @Override
    public PageDto<List<String>> getData(long id, PageRequestDto pageRequestDto) {
        log.info("Starting to get instances data with id [{}]", id);
        InstancesEntity instancesEntity = getById(id);
        return instancesService.getInstances(instancesEntity, pageRequestDto);
    }

    @Override
    public Instances getInstances(InstancesEntity instancesEntity) {
        log.info("Starting to get instances data with id [{}]", instancesEntity.getId());
        return instancesService.getInstances(instancesEntity);
    }

    @Override
    public InstancesModel getValidInstancesModel(InstancesEntity instancesEntity) {
        log.info("Starting to get valid instances model with id [{}]", instancesEntity.getId());
        return instancesService.getValidInstancesModel(instancesEntity);
    }

    @Override
    public List<AttributeDto> getAttributes(long id) {
        log.info("Gets instances [{}] attributes", id);
        InstancesEntity instancesEntity = getById(id);
        var attributes = attributeService.getAttributes(instancesEntity);
        return attributeMapper.map(attributes);
    }

    @Override
    @Audit(value = SET_CLASS_ATTRIBUTE, correlationIdKey = "#result.instancesEntity.id")
    public AttributeEntity setClassAttribute(long classAttributeId) {
        log.info("Starting to set class attribute [{}]", classAttributeId);
        var attribute = attributeService.getById(classAttributeId);
        if (!AttributeType.NOMINAL.equals(attribute.getType())) {
            throw new InvalidClassAttributeTypeException(classAttributeId);
        }
        if (attribute.getValues().size() < MIN_NUM_CLASSES) {
            throw new ClassAttributeValuesIsTooLowException(classAttributeId);
        }
        var instancesEntity = attribute.getInstancesEntity();
        instancesEntity.setClassAttribute(attribute);
        instancesEntity.increaseUpdatesCounter();
        instancesRepository.save(instancesEntity);
        log.info("Class attribute [{}] has been set for instances [{}]", classAttributeId,
                attribute.getInstancesEntity().getRelationName());
        return attribute;
    }

    @Override
    @Audit(value = SELECT_ALL_ATTRIBUTES, correlationIdKey = "#id")
    @Transactional
    public InstancesEntity selectAllAttributes(long id) {
        log.info("Starting to select all attributes for instances [{}]", id);
        var instancesEntity = getById(id);
        attributeRepository.selectAll(instancesEntity);
        instancesEntity.increaseUpdatesCounter();
        instancesRepository.save(instancesEntity);
        log.info("All attributes has been selected for instances [{}] with relation name [{}]", id,
                instancesEntity.getId());
        return instancesEntity;
    }

    @Override
    @Audit(value = RENAME_INSTANCES, correlationIdKey = "#id")
    @Locked(lockName = "saveOrUpdateDataSet", key = "#newTableName")
    @Transactional
    public String renameData(long id, String newName) {
        log.info("Starting to rename instances [{}] with new name [{}]", id, newName);
        InstancesEntity instancesEntity = getById(id);
        String oldRelationName = instancesEntity.getRelationName();
        if (!instancesEntity.getRelationName().equals(newName)) {
            if (instancesRepository.existsByRelationName(newName)) {
                throw new InstancesExistsException(newName);
            }
            instancesEntity.setRelationName(newName);
            instancesEntity.increaseUpdatesCounter();
            instancesRepository.save(instancesEntity);
            log.info("Instances [{}] has been renamed to [{}]", id, newName);
        }
        log.info("Rename instances [{}] has been finished", instancesEntity.getId());
        return oldRelationName;
    }

    @Override
    public InstancesEntity getById(long id) {
        log.debug("Gets instances by id [{}]", id);
        return instancesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(InstancesEntity.class, id));
    }

    private InstancesEntity saveInstancesEntity(String relationName, Instances instances) {
        InstancesEntity instancesEntity = new InstancesEntity();
        instancesEntity.setUuid(UUID.randomUUID().toString());
        instancesEntity.setIdColumnName(String.format(ID_COLUMN_NAME_FORMAT, RandomUtils.nextInt()));
        instancesEntity.setRelationName(relationName);
        String transformedUuid = StringUtils.replaceChars(instancesEntity.getUuid(), '-', '_');
        instancesEntity.setTableName(String.format(TABLE_NAME_FORMAT, transformedUuid));
        instancesEntity.setNumAttributes(instances.numAttributes());
        instancesEntity.setNumInstances(instances.numInstances());
        instancesEntity.setCreatedBy(userService.getCurrentUser());
        instancesEntity.setCreated(LocalDateTime.now());
        return instancesRepository.save(instancesEntity);
    }

    private void setClassAttribute(Instances instances,
                                   InstancesEntity instancesEntity,
                                   List<AttributeEntity> attributeEntities) {
        var classAttribute = instances.classAttribute();
        if (classAttribute.isNumeric()) {
            log.warn("Class attribute [{}] is numeric or date for instances [{}]. Ignore class setting",
                    classAttribute.name(), instancesEntity.getRelationName());
        } else if (classAttribute.numValues() < MIN_NUM_CLASSES) {
            log.warn("Class attribute [{}] has less than 2 values for instances [{}]. Ignore class setting",
                    classAttribute.name(), instancesEntity.getRelationName());
        } else {
            var className = classAttribute.name();
            var classAttributeEntity = attributeEntities.stream()
                    .filter(attributeEntity -> attributeEntity.getColumnName().equals(className))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("Can't find class attribute entity [%s]", className)));
            instancesEntity.setClassAttribute(classAttributeEntity);
            instancesRepository.save(instancesEntity);
            log.info("Class attribute [{}] has been set for instances [{}]", className,
                    instancesEntity.getRelationName());
        }
    }

    private void unsetClassAttribute(InstancesEntity instancesEntity) {
        var classAttribute = instancesEntity.getClassAttribute();
        if (classAttribute != null) {
            instancesEntity.setClassAttribute(null);
            instancesRepository.save(instancesEntity);
            log.info("Class [{}] attribute has been unset for instances [{}]", classAttribute.getColumnName(),
                    instancesEntity.getRelationName());
        }
    }
}
