package com.ecaservice.ers.service;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.validation.annotations.ValidPageRequest;
import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.filter.InstancesInfoFilter;
import com.ecaservice.ers.mapping.InstancesMapper;
import com.ecaservice.ers.model.InstancesInfo;
import com.ecaservice.ers.repository.InstancesInfoRepository;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.ers.dictionary.FilterDictionaries.INSTANCES_INFO;
import static com.ecaservice.ers.model.InstancesInfo_.CREATED_DATE;

/**
 * Instances service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesService {

    private final InstancesMapper instancesMapper;
    private final FilterTemplateService filterTemplateService;
    private final InstancesInfoRepository instancesInfoRepository;

    /**
     * Gets or saves instances info in case if instances with md5 hash doesn't exists.
     *
     * @param evaluationResultsRequest - evaluation results request
     * @return instances info
     */
    @Locked(lockName = "getOrSaveErsInstancesInfo", key = "#evaluationResultsRequest.instances.dataMd5Hash")
    public InstancesInfo getOrSaveInstancesInfo(EvaluationResultsRequest evaluationResultsRequest) {
        String dataMd5Hash = evaluationResultsRequest.getInstances().getDataMd5Hash();
        log.info("Starting to get instances [{}] with md5 hash [{}]",
                evaluationResultsRequest.getInstances().getRelationName(), dataMd5Hash);
        InstancesInfo instancesInfo;
        instancesInfo = instancesInfoRepository.findByDataMd5Hash(dataMd5Hash);
        if (instancesInfo == null) {
            instancesInfo = instancesMapper.map(evaluationResultsRequest.getInstances());
            instancesInfo.setCreatedDate(LocalDateTime.now());
            instancesInfoRepository.save(instancesInfo);
            log.info("New instances [{}] with md5 hash [{}] has been saved",
                    evaluationResultsRequest.getInstances().getRelationName(), dataMd5Hash);
        }
        log.info("Instances [{}] with md5 hash [{}] has been fetched",
                evaluationResultsRequest.getInstances().getRelationName(), dataMd5Hash);
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
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATED_DATE, pageRequestDto.isAscending());
        var globalFilterFields = filterTemplateService.getGlobalFilterFields(INSTANCES_INFO);
        var filter = new InstancesInfoFilter(pageRequestDto.getSearchQuery(), globalFilterFields,
                pageRequestDto.getFilters());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var instancesInfoPage = instancesInfoRepository.findAll(filter, pageRequest);
        log.info("Instances info page [{} of {}] with size [{}] has been fetched for page request [{}]",
                instancesInfoPage.getNumber(), instancesInfoPage.getTotalPages(),
                instancesInfoPage.getNumberOfElements(), pageRequestDto);
        var instancesInfoDtoList = instancesMapper.mapToInstancesInfoDtoList(instancesInfoPage.getContent());
        return PageDto.of(instancesInfoDtoList, pageRequestDto.getPage(), instancesInfoPage.getTotalElements());
    }
}
