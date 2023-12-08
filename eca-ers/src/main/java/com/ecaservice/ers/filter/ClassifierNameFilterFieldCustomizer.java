package com.ecaservice.ers.filter;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.specification.FilterFieldCustomizer;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.stream.Collectors;

import static com.ecaservice.core.filter.util.FilterUtils.buildExpression;
import static com.ecaservice.ers.model.ClassifierOptionsInfo_.CLASSIFIER_NAME;

/**
 * Classifier name filter field customizer.
 *
 * @author Roman Batygin
 */
public class ClassifierNameFilterFieldCustomizer extends FilterFieldCustomizer {

    private static final String CLASSIFIER_NAME_FIELD = "classifierInfo.classifierName";

    private final FilterTemplateService filterTemplateService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterTemplateService - filter service
     */
    public ClassifierNameFilterFieldCustomizer(FilterTemplateService filterTemplateService) {
        super(CLASSIFIER_NAME_FIELD);
        this.filterTemplateService = filterTemplateService;
    }

    @Override
    public Predicate toPredicate(Root<?> root, CriteriaBuilder criteriaBuilder, String value) {
        var classifiersDictionary = filterTemplateService.getFilterDictionary(CLASSIFIER_NAME);
        var classifierNames = classifiersDictionary.getValues()
                .stream()
                .filter(filterDictionaryValueDto -> filterDictionaryValueDto.getLabel().toLowerCase().contains(
                        value.toLowerCase()))
                .map(FilterDictionaryValueDto::getValue)
                .collect(Collectors.toList());
        Expression<?> expression = buildExpression(root, getFieldName());
        return expression.in(classifierNames);
    }
}
