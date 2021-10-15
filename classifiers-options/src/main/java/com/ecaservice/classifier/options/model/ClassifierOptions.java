package com.ecaservice.classifier.options.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

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
        @JsonSubTypes.Type(value = IterativeEnsembleOptions.class, name = ClassifierOptionsType.ITERATIVE_ENSEMBLE),
})
@Schema(subTypes = {AdaBoostOptions.class, ExtraTreesOptions.class,
        HeterogeneousClassifierOptions.class, RandomForestsOptions.class, RandomNetworkOptions.class,
        DecisionTreeOptions.class, LogisticOptions.class, KNearestNeighboursOptions.class, NeuralNetworkOptions.class,
        J48Options.class, StackingOptions.class})
public abstract class ClassifierOptions implements Serializable {
}
