package com.ecaservice.web.push.filter;

import com.ecaservice.core.filter.specification.AbstractFilter;
import com.ecaservice.web.dto.model.FilterRequestDto;
import com.ecaservice.web.push.entity.NotificationEntity;
import lombok.Getter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

import static com.ecaservice.web.push.entity.NotificationEntity_.RECEIVER;

/**
 * Implements notification filter.
 *
 * @author Roman Batygin
 */
public class NotificationFilter extends AbstractFilter<NotificationEntity> {

    @Getter
    private final String user;

    /**
     * Constructor with filters requests.
     *
     * @param user - user
     * @param globalFilterFields - global filter fields list
     * @param searchQuery        - search query string
     * @param filters            - filters requests list
     */
    public NotificationFilter(String user,
                              String searchQuery,
                              List<String> globalFilterFields,
                              List<FilterRequestDto> filters) {
        super(NotificationEntity.class, searchQuery, globalFilterFields, filters);
        this.user = user;
    }

    @Override
    public Predicate toPredicate(Root<NotificationEntity> root,
                                 CriteriaQuery<?> criteriaQuery,
                                 CriteriaBuilder criteriaBuilder) {
        var predicate = super.toPredicate(root, criteriaQuery, criteriaBuilder);
        var userExpression = root.get(RECEIVER);
        var userEqPredicate = criteriaBuilder.equal(userExpression, user);
        return criteriaBuilder.and(userEqPredicate, predicate);
    }
}
