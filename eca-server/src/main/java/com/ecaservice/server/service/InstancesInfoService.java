package com.ecaservice.server.service;

import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.filter.InstancesInfoFilter;
import com.ecaservice.server.mapping.InstancesInfoMapper;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.FilterTemplateType.INSTANCES_INFO;
import static com.ecaservice.server.model.entity.InstancesInfo_.CREATED_DATE;

/**
 * Instances info service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class InstancesInfoService {

    private final FilterService filterService;
    private final InstancesInfoMapper instancesInfoMapper;
    private final InstancesInfoRepository instancesInfoRepository;

    /**
     * Gets or save new instances info.
     *
     * @param instancesMetaDataModel - instances meta data model
     * @return instances info
     */
    @Locked(lockName = "getOrSaveInstancesInfo", key = "#instancesMetaDataModel.md5Hash")
    public InstancesInfo getOrSaveInstancesInfo(InstancesMetaDataModel instancesMetaDataModel) {
        log.info("Gets instances info [{}] with md5 hash [{}]", instancesMetaDataModel.getRelationName(),
                instancesMetaDataModel.getMd5Hash());
        var instancesInfo = instancesInfoRepository.findByDataMd5Hash(instancesMetaDataModel.getMd5Hash());
        if (instancesInfo == null) {
            instancesInfo = createAndSaveNewInstancesInfo(instancesMetaDataModel);
            log.info("New instances info [{}] has been saved with md5 hash [{}]", instancesMetaDataModel.getRelationName(),
                    instancesMetaDataModel.getMd5Hash());
        }
        log.info("Instances info [{}] with md5 hash [{}] has been fetched", instancesMetaDataModel.getRelationName(),
                instancesMetaDataModel.getMd5Hash());
        return instancesInfo;
    }

    public PageDto<InstancesInfoDto> getNextPage(
            @ValidPageRequest(filterTemplateName = INSTANCES_INFO) PageRequestDto pageRequestDto) {
        log.info("Gets instances info next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATED_DATE, pageRequestDto.isAscending());
        var globalFilterFields = filterService.getGlobalFilterFields(INSTANCES_INFO);
        var filter = new InstancesInfoFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var instancesInfoPage = instancesInfoRepository.findAll(filter, pageRequest);
        log.info("Instances info page [{} of {}] with size [{}] has been fetched for page request [{}]",
                instancesInfoPage.getNumber(), instancesInfoPage.getTotalPages(),
                instancesInfoPage.getNumberOfElements(), pageRequestDto);
        var instancesInfoDtoList = instancesInfoMapper.map(instancesInfoPage.getContent());
        return PageDto.of(instancesInfoDtoList, pageRequestDto.getPage(), instancesInfoPage.getTotalElements());
    }

    private InstancesInfo createAndSaveNewInstancesInfo(InstancesMetaDataModel instancesMetaDataModel) {
        var instancesInfo = new InstancesInfo();
        instancesInfo.setRelationName(instancesMetaDataModel.getRelationName());
        instancesInfo.setNumInstances(instancesMetaDataModel.getNumInstances());
        instancesInfo.setNumAttributes(instancesMetaDataModel.getNumAttributes());
        instancesInfo.setNumClasses(instancesMetaDataModel.getNumClasses());
        instancesInfo.setClassName(instancesMetaDataModel.getClassName());
        instancesInfo.setDataMd5Hash(instancesMetaDataModel.getMd5Hash());
        instancesInfo.setUuid(UUID.randomUUID().toString());
        instancesInfo.setCreatedDate(LocalDateTime.now());
        return instancesInfoRepository.save(instancesInfo);
    }
}
