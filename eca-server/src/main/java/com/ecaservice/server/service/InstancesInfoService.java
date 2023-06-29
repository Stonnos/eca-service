package com.ecaservice.server.service;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.filter.InstancesInfoFilter;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.entity.InstancesInfo_;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.ecaservice.core.filter.util.FilterUtils.buildSort;
import static com.ecaservice.server.model.entity.InstancesInfo_.CREATED_DATE;

/**
 * Instances info service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstancesInfoService implements PageRequestService<InstancesInfo> {

    private static final List<String> INSTANCES_INFO_GLOBAL_FILTER_FIELDS =
            Collections.singletonList(InstancesInfo_.RELATION_NAME);

    private final InstancesInfoRepository instancesInfoRepository;

    /**
     * Gets or save new instances info.
     *
     * @param dataMd5Hash - data md5 hash
     * @param data        - instances object
     * @return instances info
     */
    @Locked(lockName = "getOrSaveInstancesInfo", key = "#dataMd5Hash")
    public InstancesInfo getOrSaveInstancesInfo(String dataMd5Hash, Instances data) {
        log.info("Gets instances info [{}] with md5 hash [{}]", data.relationName(), dataMd5Hash);
        var instancesInfo = instancesInfoRepository.findByDataMd5Hash(dataMd5Hash);
        if (instancesInfo == null) {
            instancesInfo = createAndSaveNewInstancesInfo(dataMd5Hash, data);
            log.info("New instances info [{}] has been saved with md5 hash [{}]", data.relationName(), dataMd5Hash);
        }
        log.info("Instances info [{}] with md5 hash [{}] has been fetched", data.relationName(), dataMd5Hash);
        return instancesInfo;
    }

    @Override
    public Page<InstancesInfo> getNextPage(PageRequestDto pageRequestDto) {
        log.info("Gets instances info next page: {}", pageRequestDto);
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATED_DATE, pageRequestDto.isAscending());
        var filter = new InstancesInfoFilter(pageRequestDto.getSearchQuery(), INSTANCES_INFO_GLOBAL_FILTER_FIELDS,
                pageRequestDto.getFilters());
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
        var instancesInfoPage = instancesInfoRepository.findAll(filter, pageRequest);
        log.info("Instances info page [{} of {}] with size [{}] has been fetched for page request [{}]",
                instancesInfoPage.getNumber(), instancesInfoPage.getTotalPages(),
                instancesInfoPage.getNumberOfElements(), pageRequestDto);
        return instancesInfoPage;
    }

    private InstancesInfo createAndSaveNewInstancesInfo(String dataMd5Hash, Instances data) {
        var instancesInfo = new InstancesInfo();
        instancesInfo.setRelationName(data.relationName());
        instancesInfo.setNumInstances(data.numInstances());
        instancesInfo.setNumAttributes(data.numAttributes());
        instancesInfo.setNumClasses(data.numClasses());
        instancesInfo.setClassName(data.classAttribute().name());
        instancesInfo.setDataMd5Hash(dataMd5Hash);
        instancesInfo.setUuid(UUID.randomUUID().toString());
        instancesInfo.setCreatedDate(LocalDateTime.now());
        return instancesInfoRepository.save(instancesInfo);
    }
}
