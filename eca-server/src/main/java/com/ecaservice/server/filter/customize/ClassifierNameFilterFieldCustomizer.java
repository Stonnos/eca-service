package com.ecaservice.server.filter.customize;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.core.filter.specification.FilterFieldCustomizer;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import static com.ecaservice.core.filter.util.FilterTemplateUtils.findValuesByLabel;
import static com.ecaservice.core.filter.util.FilterUtils.buildExpression;
import static com.ecaservice.server.service.filter.dictionary.FilterDictionaries.CLASSIFIER_NAME;

/**
 * Classifier name filter field customizer.
 *
 * @author Roman Batygin
 */
public class ClassifierNameFilterFieldCustomizer extends FilterFieldCustomizer {

    private static final String CLASSIFIER_NAME_FIELD = "classifierName";

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
        var classifierNames = findValuesByLabel(classifiersDictionary, value);
        Expression<?> expression = buildExpression(root, getFieldName());
        return expression.in(classifierNames);
    }
}
