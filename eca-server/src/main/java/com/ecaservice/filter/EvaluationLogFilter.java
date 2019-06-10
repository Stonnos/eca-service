package com.ecaservice.filter;

import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationLog_;
import com.ecaservice.web.dto.model.FilterRequestDto;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.MessageFormat;
import java.util.List;

import static com.ecaservice.util.Utils.splitByPointSeparator;

/**
 * Implements evaluation log filter.
 *
 * @author Roman Batygin
 */
public class EvaluationLogFilter extends AbstractFilter<EvaluationLog> {

    /**
     * Constructor with filters requests.
     *
     * @param filters - filters requests list
     */
    public EvaluationLogFilter(List<FilterRequestDto> filters) {
        super(EvaluationLog.class, filters);
    }

    @Override
    protected Predicate buildLikePredicate(FilterRequestDto filterRequestDto, Root<EvaluationLog> root,
                                           CriteriaBuilder criteriaBuilder) {
        String[] fieldLevels = splitByPointSeparator(filterRequestDto.getName());
        if (fieldLevels.length > 1) {
            switch (fieldLevels[0]) {
                case EvaluationLog_.INSTANCES_INFO:
                    Join<EvaluationLog, ?> instancesInfoJoin = root.join(EvaluationLog_.INSTANCES_INFO);
                    return criteriaBuilder.like(criteriaBuilder.lower(instancesInfoJoin.get(fieldLevels[1])),
                            MessageFormat.format("%{0}%", filterRequestDto.getValue().trim().toLowerCase()));
                default:
                    throw new IllegalArgumentException(
                            String.format("Unexpected entity field name: %s", fieldLevels[0]));
            }
        }
        return super.buildLikePredicate(filterRequestDto, root, criteriaBuilder);
    }
}
