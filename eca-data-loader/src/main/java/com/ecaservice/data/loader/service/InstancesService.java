package com.ecaservice.data.loader.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.data.loader.dto.InstancesMetaInfoDto;
import com.ecaservice.data.loader.entity.InstancesEntity;
import com.ecaservice.data.loader.exception.ExpiredDataException;
import com.ecaservice.data.loader.mapping.InstancesMapper;
import com.ecaservice.data.loader.repository.InstancesRepository;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service to manage with instances.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesService {

    private final InstancesMapper instancesMapper;
    private final ObjectStorageService objectStorageService;
    private final InstancesRepository instancesRepository;

    /**
     * Gets instances by uuid.
     *
     * @param uuid - instances uuid
     * @return instances entity
     */
    public InstancesEntity getByUuid(String uuid) {
        log.debug("Gets instances by uuid [{}]", uuid);
        return instancesRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException(InstancesEntity.class, uuid));
    }

    /**
     * Gets instances meta info.
     *
     * @param uuid - instances uuid
     * @return instances meta info dto
     */
    public InstancesMetaInfoDto getMetaInfo(String uuid) {
        log.info("Gets instances meta info  by uuid [{}]", uuid);
        var instancesEntity = getByUuid(uuid);
        if (LocalDateTime.now().isAfter(instancesEntity.getExpireAt())) {
            throw new ExpiredDataException(String.format("Instances [%s] object has been expired", uuid));
        }
        return instancesMapper.map(instancesEntity);
    }

    /**
     * Deletes instances entity.
     *
     * @param instancesEntity - instances entity
     */
    public void deleteInstances(InstancesEntity instancesEntity) {
        try {
            log.info("Starting to delete instances [{}]", instancesEntity.getUuid());
            objectStorageService.removeObject(instancesEntity.getObjectPath());
            instancesRepository.delete(instancesEntity);
            log.info("Instances [{}] has been deleted", instancesEntity.getUuid());
        } catch (Exception ex) {
            log.error("There was an error while deleting instances [{}]: {}", instancesEntity.getUuid(),
                    ex.getMessage());
        }
    }
}
