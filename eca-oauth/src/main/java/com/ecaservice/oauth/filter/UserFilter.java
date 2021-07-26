package com.ecaservice.oauth.filter;

import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.web.dto.model.FilterRequestDto;

import java.util.List;

/**
 * Implements user filter.
 *
 * @author Roman Batygin
 */
public class UserFilter extends AbstractFilter<UserEntity> {

    /**
     * Constructor with filters requests.
     *
     * @param globalFilterFields - global filter fields list
     * @param searchQuery        - search query string
     * @param filters            - filters requests list
     */
    public UserFilter(String searchQuery, List<String> globalFilterFields, List<FilterRequestDto> filters) {
        super(UserEntity.class, searchQuery, globalFilterFields, filters);
    }
}
