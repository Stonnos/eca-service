package com.ecaservice.classifier.options.adapter;

import com.ecaservice.classifier.options.TestHelperUtils;
import com.ecaservice.classifier.options.config.ClassifiersOptionsAutoConfiguration;
import com.ecaservice.classifier.options.config.ClassifiersOptionsConfig;
import com.ecaservice.classifier.options.model.AdaBoostOptions;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.classifier.options.model.DecisionTreeOptions;
import com.ecaservice.classifier.options.model.ExtraTreesOptions;
import com.ecaservice.classifier.options.model.HeterogeneousClassifierOptions;
import com.ecaservice.classifier.options.model.J48Options;
import com.ecaservice.classifier.options.model.KNearestNeighboursOptions;
import com.ecaservice.classifier.options.model.LogisticOptions;
import com.ecaservice.classifier.options.model.NeuralNetworkOptions;
import com.ecaservice.classifier.options.model.RandomForestsOptions;
import com.ecaservice.classifier.options.model.RandomNetworkOptions;
import com.ecaservice.classifier.options.model.StackingOptions;
import eca.ensemble.AdaBoostClassifier;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.RandomNetworks;
import eca.ensemble.StackingClassifier;
import eca.ensemble.forests.DecisionTreeType;
import eca.ensemble.forests.ExtraTreesClassifier;
import eca.ensemble.forests.RandomForests;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.ChebyshevDistance;
import eca.metrics.distances.SquareEuclidDistance;
import eca.neural.NeuralNetwork;
import eca.neural.functions.HyperbolicTangentFunction;
import eca.neural.functions.LogisticFunction;
import eca.neural.functions.SoftSignFunction;
import eca.regression.Logistic;
import eca.trees.CART;
import eca.trees.J48;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.BayesNet;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link ClassifierOptionsAdapter} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ClassifiersOptionsAutoConfiguration.class, ClassifierOptionsAdapter.class})
class ClassifierOptionsAdapterTest {

    @Inject
    private ClassifierOptionsAdapter classifierOptionsAdapter;
    @Inject
    private ClassifiersOptionsConfig classifiersOptionsConfig;

    @Test
    void testUnsupportedClassifier() {
        BayesNet bayesNet = new BayesNet();
        assertThrows(IllegalStateException.class, () -> classifierOptionsAdapter.convert(bayesNet));
    }

    @Test
    void testMapLogistic() {
        ClassifierOptions options = classifierOptionsAdapter.convert(new Logistic());
        assertThat(options).isInstanceOf(LogisticOptions.class);
    }

    @Test
    void testDecisionTree() {
        ClassifierOptions options = classifierOptionsAdapter.convert(new CART());
        assertThat(options).isInstanceOf(DecisionTreeOptions.class);
    }

    @Test
    void testMapJ48() {
        ClassifierOptions options = classifierOptionsAdapter.convert(new J48());
        assertThat(options).isInstanceOf(J48Options.class);
    }

    @Test
    void testMapKNN() {
        ClassifierOptions options = classifierOptionsAdapter.convert(new KNearestNeighbours());
        assertThat(options).isInstanceOf(KNearestNeighboursOptions.class);
    }

    @Test
    void testMapNeuralNetwork() {
        ClassifierOptions options = classifierOptionsAdapter.convert(new NeuralNetwork());
        assertThat(options).isInstanceOf(NeuralNetworkOptions.class);
    }

    @Test
    void testMapHeterogeneousClassifier() {
        HeterogeneousClassifier heterogeneousClassifier = new HeterogeneousClassifier();
        heterogeneousClassifier.setClassifiersSet(new ClassifiersSet());
        heterogeneousClassifier.getClassifiersSet().addClassifier(TestHelperUtils.createJ48());
        heterogeneousClassifier.getClassifiersSet().addClassifier(
                TestHelperUtils.createNeuralNetwork(new LogisticFunction()));
        heterogeneousClassifier.getClassifiersSet().addClassifier(
                TestHelperUtils.createKNearestNeighbours(new ChebyshevDistance()));
        heterogeneousClassifier.getClassifiersSet().addClassifier(new Logistic());
        heterogeneousClassifier.getClassifiersSet().addClassifier(
                TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.ID3));
        ClassifierOptions options = classifierOptionsAdapter.convert(heterogeneousClassifier);
        assertThat(options).isInstanceOf(HeterogeneousClassifierOptions.class);
        HeterogeneousClassifierOptions heterogeneousClassifierOptions = (HeterogeneousClassifierOptions) options;
        assertThat(heterogeneousClassifierOptions.getClassifierOptions()).isNotEmpty();
        assertThat(heterogeneousClassifierOptions.getClassifierOptions()).hasSameSizeAs(
                heterogeneousClassifier.getClassifiersSet());
    }

    @Test
    void testMapAdaBoost() {
        AdaBoostClassifier adaBoostClassifier = new AdaBoostClassifier();
        adaBoostClassifier.setClassifiersSet(new ClassifiersSet());
        adaBoostClassifier.getClassifiersSet().addClassifier(
                TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.CHAID));
        adaBoostClassifier.getClassifiersSet().addClassifier(
                TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.CART));
        adaBoostClassifier.getClassifiersSet().addClassifier(
                TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.C45));
        ClassifierOptions options = classifierOptionsAdapter.convert(adaBoostClassifier);
        assertThat(options).isInstanceOf(AdaBoostOptions.class);
        AdaBoostOptions adaBoostOptions = (AdaBoostOptions) options;
        assertThat(adaBoostOptions.getClassifierOptions()).isNotEmpty();
        assertThat(adaBoostOptions.getClassifierOptions()).hasSameSizeAs(
                adaBoostOptions.getClassifierOptions());
    }

    @Test
    void testMapStacking() {
        StackingClassifier stackingClassifier = TestHelperUtils.createStackingClassifier();
        stackingClassifier.setMetaClassifier(TestHelperUtils.createNeuralNetwork(new SoftSignFunction()));
        stackingClassifier.setClassifiers(new ClassifiersSet());
        stackingClassifier.getClassifiers().addClassifier(TestHelperUtils.createJ48());
        stackingClassifier.getClassifiers().addClassifier(
                TestHelperUtils.createKNearestNeighbours(new SquareEuclidDistance()));
        stackingClassifier.getClassifiers().addClassifier(
                TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.ID3));
        stackingClassifier.getClassifiers().addClassifier(new Logistic());
        stackingClassifier.getClassifiers().addClassifier(
                TestHelperUtils.createNeuralNetwork(new HyperbolicTangentFunction()));
        ClassifierOptions options = classifierOptionsAdapter.convert(stackingClassifier);
        assertThat(options).isInstanceOf(StackingOptions.class);
        StackingOptions stackingOptions = (StackingOptions) options;
        assertThat(stackingOptions.getMetaClassifierOptions()).isNotNull();
        assertThat(stackingOptions.getMetaClassifierOptions()).isInstanceOf(NeuralNetworkOptions.class);
        assertThat(stackingOptions.getClassifierOptions()).isNotEmpty();
        assertThat(stackingOptions.getClassifierOptions()).hasSameSizeAs(
                stackingClassifier.getClassifiers());
    }

    @Test
    void testMapRandomForests() {
        ClassifierOptions options = classifierOptionsAdapter.convert(new RandomForests());
        assertThat(options).isInstanceOf(RandomForestsOptions.class);
    }

    @Test
    void testMapExtraTrees() {
        ClassifierOptions options = classifierOptionsAdapter.convert(new ExtraTreesClassifier());
        assertThat(options).isInstanceOf(ExtraTreesOptions.class);
    }

    @Test
    void testMapRandomNetworks() {
        ClassifierOptions options = classifierOptionsAdapter.convert(new RandomNetworks());
        assertThat(options).isInstanceOf(RandomNetworkOptions.class);
    }

    @Test
    void testMapHeterogeneousClassifierOptions() {
        HeterogeneousClassifierOptions heterogeneousClassifierOptions =
                TestHelperUtils.createHeterogeneousClassifierOptions(false);
        AbstractClassifier classifier = classifierOptionsAdapter.convert(heterogeneousClassifierOptions);
        assertThat(classifier).isInstanceOf(HeterogeneousClassifier.class);
        HeterogeneousClassifier heterogeneousClassifier = (HeterogeneousClassifier) classifier;
        assertThat(heterogeneousClassifier.getClassifiersSet().size()).isEqualTo(
                heterogeneousClassifierOptions.getClassifierOptions().size());
    }

    @Test
    void testMapStackingClassifierOptions() {
        StackingOptions stackingOptions = TestHelperUtils.createStackingOptions();
        AbstractClassifier classifier = classifierOptionsAdapter.convert(stackingOptions);
        assertThat(classifier).isInstanceOf(StackingClassifier.class);
        StackingClassifier stackingClassifier = (StackingClassifier) classifier;
        assertThat(stackingClassifier.getClassifiers().size()).isEqualTo(
                stackingOptions.getClassifierOptions().size());
        assertThat(stackingClassifier.getMetaClassifier()).isInstanceOf(J48.class);
    }

    @Test
    void testMapKnnOptions() {
        KNearestNeighboursOptions kNearestNeighboursOptions = TestHelperUtils.createKNearestNeighboursOptions();
        AbstractClassifier classifier = classifierOptionsAdapter.convert(kNearestNeighboursOptions);
        assertThat(classifier).isInstanceOf(KNearestNeighbours.class);
        KNearestNeighbours kNearestNeighbours = (KNearestNeighbours) classifier;
        assertThat(kNearestNeighbours.getDecimalFormat().getMaximumFractionDigits()).isEqualTo(
                classifiersOptionsConfig.getMaximumFractionDigits());
    }
}
