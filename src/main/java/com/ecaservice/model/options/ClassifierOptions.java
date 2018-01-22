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
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes( {
        @JsonSubTypes.Type(value = DecisionTreeOptions.class, name = "decision_tree"),
        @JsonSubTypes.Type(value = LogisticOptions.class, name = "logistic"),
        @JsonSubTypes.Type(value = KNearestNeighboursOptions.class, name = "knn"),
        @JsonSubTypes.Type(value = NeuralNetworkOptions.class, name = "neural_network"),
        @JsonSubTypes.Type(value = J48Options.class, name = "j48"),
})
public abstract class ClassifierOptions {
}
