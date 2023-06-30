package com.ecaservice.classifier.options.mapping;

import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import eca.ensemble.forests.DecisionTreeType;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.DecisionTreeClassifier;
import eca.trees.ID3;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.core.annotation.Order;

import java.util.Map;

import static com.google.common.collect.Maps.newLinkedHashMap;

/**
 * Implements mapping decision tree classifier to its options model.
 *
 * @author Roman Batygin
 */
@Order(Ordered.DECISION_TREE_ORDER)
@Mapper
public abstract class DecisionTreeMapper extends AbstractClassifierMapper<DecisionTreeClassifier, DecisionTreeOptions> {

    private final Map<Class<?>, DecisionTreeType> decisionTreeTypeMap = newLinkedHashMap();

    protected DecisionTreeMapper() {
        super(DecisionTreeClassifier.class);
        decisionTreeTypeMap.put(CART.class, DecisionTreeType.CART);
        decisionTreeTypeMap.put(C45.class, DecisionTreeType.C45);
        decisionTreeTypeMap.put(ID3.class, DecisionTreeType.ID3);
        decisionTreeTypeMap.put(CHAID.class, DecisionTreeType.CHAID);
    }

    @AfterMapping
    protected void mapDecisionTreeType(DecisionTreeClassifier classifier, @MappingTarget DecisionTreeOptions options) {
        DecisionTreeType decisionTreeType = decisionTreeTypeMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().isAssignableFrom(classifier.getClass()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Unsupported decision tree type: %s!", classifier.getClass().getSimpleName())));
        options.setDecisionTreeType(decisionTreeType);
    }

    @AfterMapping
    protected void mapAdditionalOptions(DecisionTreeClassifier classifier, @MappingTarget DecisionTreeOptions options) {
        if (classifier instanceof CHAID) {
            CHAID chaid = (CHAID) classifier;
            options.setAlpha(chaid.getAlpha());
        }
    }
}
