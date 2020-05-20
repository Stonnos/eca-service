package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.CommonConfig;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.ecaservice.oauth.entity.UserEntity_.CREATION_DATE;
import static com.ecaservice.oauth.util.FilterUtils.buildSort;
import static com.ecaservice.oauth.util.FilterUtils.buildSpecification;

/**
 * Users management service.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final CommonConfig commonConfig;
    private final UserEntityRepository userEntityRepository;

    /**
     * Gets the next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @return entities page
     */
    public Page<UserEntity> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        Specification<UserEntity> specification = buildSpecification(pageRequestDto);
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return userEntityRepository.findAll(specification, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }
}
