package com.ecaservice.data.storage.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.data.loader.dto.UploadInstancesResponseDto;
import com.ecaservice.data.storage.dto.ExportInstancesResponseDto;
import com.ecaservice.data.storage.entity.ExportInstancesObjectEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.data.storage.repository.ExportInstancesObjectRepository;
import com.ecaservice.data.storage.repository.InstancesRepository;
import com.ecaservice.data.storage.service.data.UploadInstancesObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for exporting instances to central data storage.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportInstancesObjectService {

    private final StorageService storageService;
    private final UploadInstancesObjectService uploadInstancesObjectService;
    private final InstancesRepository instancesRepository;
    private final ExportInstancesObjectRepository exportInstancesObjectRepository;

    /**
     * Exports valid instances with selected attributes and assigned class attribute to central data storage.
     *
     * @param uuid - instances uuid
     */
    public ExportInstancesResponseDto exportValidInstances(String uuid) {
        log.info("Starting to export instances [{}] to central data storage", uuid);
        var instancesEntity = instancesRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException(InstancesEntity.class, uuid));
        var lastExportInstancesObject =
                exportInstancesObjectRepository.findFirstByInstancesUuidOrderByCreatedDesc(uuid);
        if (lastExportInstancesObject == null) {
            log.info("No one export instances [{}] object has been found. Starting to export new instances", uuid);
            return exportValidInstances(instancesEntity);
        } else if (LocalDateTime.now().isAfter(lastExportInstancesObject.getExpireAt())) {
            log.info("Instances [{}] data has been expired. Starting to export new instances", uuid);
            return exportValidInstances(instancesEntity);
        } else if (instancesEntity.getUpdatesCounter() != lastExportInstancesObject.getUpdatesCounter()) {
            log.info("Instances [{}] data has been changed. Starting to export new instances", uuid);
            return exportValidInstances(instancesEntity);
        } else {
            log.info("Last export instances [{}] object with external uuid [{}] has been fetched from cache", uuid,
                    lastExportInstancesObject.getExternalDataUuid());
            return ExportInstancesResponseDto.builder()
                    .externalDataUuid(lastExportInstancesObject.getExternalDataUuid())
                    .build();
        }
    }

    private ExportInstancesResponseDto exportValidInstances(InstancesEntity instancesEntity) {
        var instancesModel = storageService.getValidInstancesModel(instancesEntity);
        var uploadInstancesResponseDto =
                uploadInstancesObjectService.uploadInstances(instancesEntity.getUuid(), instancesModel);
        createAndSaveExportInstancesObject(instancesEntity, uploadInstancesResponseDto);
        return ExportInstancesResponseDto.builder()
                .externalDataUuid(uploadInstancesResponseDto.getUuid())
                .build();
    }

    private void createAndSaveExportInstancesObject(InstancesEntity instancesEntity,
                                                    UploadInstancesResponseDto uploadInstancesResponseDto) {
        var exportInstancesObjectEntity = new ExportInstancesObjectEntity();
        exportInstancesObjectEntity.setInstancesUuid(instancesEntity.getUuid());
        exportInstancesObjectEntity.setExternalDataUuid(uploadInstancesResponseDto.getUuid());
        exportInstancesObjectEntity.setMd5Hash(uploadInstancesResponseDto.getMd5Hash());
        exportInstancesObjectEntity.setUpdatesCounter(instancesEntity.getUpdatesCounter());
        exportInstancesObjectEntity.setExpireAt(uploadInstancesResponseDto.getExpireAt());
        exportInstancesObjectEntity.setCreated(LocalDateTime.now());
        exportInstancesObjectRepository.save(exportInstancesObjectEntity);
    }
}
