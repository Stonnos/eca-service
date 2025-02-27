package com.ecaservice.server.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.server.filter.InstancesInfoFilter;
import com.ecaservice.server.mapping.InstancesInfoMapper;
import com.ecaservice.server.model.data.AttributeMetaInfo;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.repository.AttributesInfoRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.web.dto.model.AttributeMetaInfoDto;
import com.ecaservice.web.dto.model.AttributeValueMetaInfoDto;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.IntStream;

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

    private final FilterTemplateService filterTemplateService;
    private final InstancesInfoMapper instancesInfoMapper;
    private final InstancesProvider instancesProvider;
    private final InstancesInfoRepository instancesInfoRepository;
    private final AttributesInfoRepository attributesInfoRepository;

    /**
     * Gets or save new instances info.
     *
     * @param dataUuid - instances uuid
     * @return instances info
     */
    public InstancesInfo getOrSaveInstancesInfo(String dataUuid) {
        var instancesInfo = instancesInfoRepository.findByUuid(dataUuid);
        // Gets or save instances via double check locking
        if (instancesInfo == null) {
            instancesInfo = instancesProvider.getOrSaveInstancesInfo(dataUuid);
        }
        return instancesInfo;
    }

    /**
     * Gets instances info page.
     *
     * @param pageRequestDto - page request dto
     * @return instances info page
     */
    public PageDto<InstancesInfoDto> getNextPage(
            @ValidPageRequest(filterTemplateName = INSTANCES_INFO) PageRequestDto pageRequestDto) {
        log.info("Gets instances info next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortFields(), CREATED_DATE, true);
        var globalFilterFields = filterTemplateService.getGlobalFilterFields(INSTANCES_INFO);
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

    /**
     * Gets instances class values.
     *
     * @param instancesId - instances id
     * @return class values
     */
    public List<AttributeValueMetaInfoDto> getClassValues(Long instancesId) {
        log.info("Starting to get instances [{}] class values", instancesId);
        InstancesInfo instancesInfo = instancesInfoRepository.findById(instancesId)
                .orElseThrow(() -> new EntityNotFoundException(InstancesInfo.class, instancesId));
        var attributesInfo = attributesInfoRepository.findByInstancesInfo(instancesInfo)
                .orElseThrow(() -> new EntityNotFoundException(InstancesInfo.class, instancesInfo.getId()));
        AttributeMetaInfo classAttributeInfo = attributesInfo.getAttributes()
                .stream()
                .filter(attributeMetaInfo -> attributeMetaInfo.getName().equals(instancesInfo.getClassName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't find class attribute [%s] in attributes list",
                                instancesInfo.getClassName())));
        var classValuesMetaInfo = IntStream.range(0, classAttributeInfo.getValues().size()).mapToObj(index -> {
            AttributeValueMetaInfoDto attributeValueMetaInfoDto = new AttributeValueMetaInfoDto();
            attributeValueMetaInfoDto.setIndex(index);
            attributeValueMetaInfoDto.setValue(classAttributeInfo.getValues().get(index));
            return attributeValueMetaInfoDto;
        }).toList();
        log.info("Class values has been fetched for instances [{}]", instancesId);
        return classValuesMetaInfo;
    }

    /**
     * Gets input attributes (without class) info list for specified instances.
     *
     * @param instancesId - instances id
     * @return input attributes meta info list
     */
    public List<AttributeMetaInfoDto> getInputAttributes(Long instancesId) {
        log.info("Starting to get instances [{}] input attributes info", instancesId);
        InstancesInfo instancesInfo = instancesInfoRepository.findById(instancesId)
                .orElseThrow(() -> new EntityNotFoundException(InstancesInfo.class, instancesId));
        var attributesInfo = attributesInfoRepository.findByInstancesInfo(instancesInfo)
                .orElseThrow(() -> new EntityNotFoundException(InstancesInfo.class, instancesInfo.getId()));
        var attributesMetaInfoList = instancesInfoMapper.mapList(attributesInfo.getAttributes())
                .stream()
                .filter(attributeMetaInfoDto -> !attributeMetaInfoDto.getName().equals(instancesInfo.getClassName()))
                .toList();
        log.info("[{}] input attributes info has been fetched for instances [{}]", attributesMetaInfoList.size(),
                instancesId);
        return attributesMetaInfoList;
    }
}
