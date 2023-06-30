package com.ecaservice.classifier.options.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

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
        @JsonSubTypes.Type(value = RandomForestsOptions.class, name = ClassifierOptionsType.RANDOM_FORESTS),
        @JsonSubTypes.Type(value = RandomNetworkOptions.class, name = ClassifierOptionsType.RANDOM_NETWORKS),
        @JsonSubTypes.Type(value = AdaBoostOptions.class, name = ClassifierOptionsType.ADA_BOOST),
        @JsonSubTypes.Type(value = HeterogeneousClassifierOptions.class, name = ClassifierOptionsType.HEC),
        @JsonSubTypes.Type(value = ExtraTreesOptions.class, name = ClassifierOptionsType.EXTRA_TREES)
})
public abstract class ClassifierOptions implements Serializable {
}
