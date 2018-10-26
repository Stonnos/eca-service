package com.ecaservice.model.options;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Classifier options abstract model.
 *
 * @author Roman Batygin
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DecisionTreeOptions.class, name = ClassifierOptionsType.DECISION_TREE),
        @JsonSubTypes.Type(value = LogisticOptions.class, name = ClassifierOptionsType.LOGISTIC),
        @JsonSubTypes.Type(value = KNearestNeighboursOptions.class, name = ClassifierOptionsType.KNN),
        @JsonSubTypes.Type(value = NeuralNetworkOptions.class, name = ClassifierOptionsType.NEURAL_NETWORK),
        @JsonSubTypes.Type(value = J48Options.class, name = ClassifierOptionsType.J48),
        @JsonSubTypes.Type(value = StackingOptions.class, name = ClassifierOptionsType.STACKING),
        @JsonSubTypes.Type(value = IterativeEnsembleOptions.class, name = ClassifierOptionsType.ITERATIVE_ENSEMBLE),
})
public abstract class ClassifierOptions {
}
