package com.ecaservice.service;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.model.options.AdaBoostOptions;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.model.options.DecisionTreeOptions;
import com.ecaservice.model.options.ExtraTreesOptions;
import com.ecaservice.model.options.HeterogeneousClassifierOptions;
import com.ecaservice.model.options.J48Options;
import com.ecaservice.model.options.KNearestNeighboursOptions;
import com.ecaservice.model.options.LogisticOptions;
import com.ecaservice.model.options.NeuralNetworkOptions;
import com.ecaservice.model.options.RandomForestsOptions;
import com.ecaservice.model.options.RandomNetworkOptions;
import com.ecaservice.model.options.StackingOptions;
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
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import weka.classifiers.bayes.BayesNet;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link ClassifierOptionsService} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClassifierOptionsServiceTest {

    @Inject
    private ClassifierOptionsService classifierOptionsService;

    @Test(expected = UnsupportedOperationException.class)
    public void testUnsupportedClassifier() {
        classifierOptionsService.convert(new BayesNet());
    }

    @Test
    public void testMapLogistic() {
        ClassifierOptions options = classifierOptionsService.convert(new Logistic());
        Assertions.assertThat(options).isInstanceOf(LogisticOptions.class);
    }

    @Test
    public void testDecisionTreeLogistic() {
        ClassifierOptions options = classifierOptionsService.convert(new CART());
        Assertions.assertThat(options).isInstanceOf(DecisionTreeOptions.class);
    }

    @Test
    public void testMapJ48() {
        ClassifierOptions options = classifierOptionsService.convert(new J48());
        Assertions.assertThat(options).isInstanceOf(J48Options.class);
    }

    @Test
    public void testMapKNN() {
        ClassifierOptions options = classifierOptionsService.convert(new KNearestNeighbours());
        Assertions.assertThat(options).isInstanceOf(KNearestNeighboursOptions.class);
    }

    @Test
    public void testMapNeuralNetwork() {
        ClassifierOptions options = classifierOptionsService.convert(new NeuralNetwork());
        Assertions.assertThat(options).isInstanceOf(NeuralNetworkOptions.class);
    }

    @Test
    public void testMapHeterogeneousClassifier() {
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
        ClassifierOptions options = classifierOptionsService.convert(heterogeneousClassifier);
        Assertions.assertThat(options).isInstanceOf(HeterogeneousClassifierOptions.class);
        HeterogeneousClassifierOptions heterogeneousClassifierOptions = (HeterogeneousClassifierOptions) options;
        Assertions.assertThat(heterogeneousClassifierOptions.getClassifierOptions()).isNotEmpty();
        Assertions.assertThat(heterogeneousClassifierOptions.getClassifierOptions().size()).isEqualTo(
                heterogeneousClassifier.getClassifiersSet().size());
    }

    @Test
    public void testMapAdaBoost() {
        AdaBoostClassifier adaBoostClassifier = new AdaBoostClassifier();
        adaBoostClassifier.setClassifiersSet(new ClassifiersSet());
        adaBoostClassifier.getClassifiersSet().addClassifier(
                TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.CHAID));
        adaBoostClassifier.getClassifiersSet().addClassifier(
                TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.CART));
        adaBoostClassifier.getClassifiersSet().addClassifier(
                TestHelperUtils.createDecisionTreeClassifier(DecisionTreeType.C45));
        ClassifierOptions options = classifierOptionsService.convert(adaBoostClassifier);
        Assertions.assertThat(options).isInstanceOf(AdaBoostOptions.class);
        AdaBoostOptions adaBoostOptions = (AdaBoostOptions) options;
        Assertions.assertThat(adaBoostOptions.getClassifierOptions()).isNotEmpty();
        Assertions.assertThat(adaBoostOptions.getClassifierOptions().size()).isEqualTo(
                adaBoostOptions.getClassifierOptions().size());
    }

    @Test
    public void testMapStacking() {
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
        ClassifierOptions options = classifierOptionsService.convert(stackingClassifier);
        Assertions.assertThat(options).isInstanceOf(StackingOptions.class);
        StackingOptions stackingOptions = (StackingOptions) options;
        Assertions.assertThat(stackingOptions.getMetaClassifierOptions()).isNotNull();
        Assertions.assertThat(stackingOptions.getMetaClassifierOptions()).isInstanceOf(NeuralNetworkOptions.class);
        Assertions.assertThat(stackingOptions.getClassifierOptions()).isNotEmpty();
        Assertions.assertThat(stackingOptions.getClassifierOptions().size()).isEqualTo(
                stackingClassifier.getClassifiers().size());
    }

    @Test
    public void testMapRandomForests() {
        ClassifierOptions options = classifierOptionsService.convert(new RandomForests());
        Assertions.assertThat(options).isInstanceOf(RandomForestsOptions.class);
    }

    @Test
    public void testMapExtraTrees() {
        ClassifierOptions options = classifierOptionsService.convert(new ExtraTreesClassifier());
        Assertions.assertThat(options).isInstanceOf(ExtraTreesOptions.class);
    }

    @Test
    public void testMapRandomNetworks() {
        ClassifierOptions options = classifierOptionsService.convert(new RandomNetworks());
        Assertions.assertThat(options).isInstanceOf(RandomNetworkOptions.class);
    }
}
