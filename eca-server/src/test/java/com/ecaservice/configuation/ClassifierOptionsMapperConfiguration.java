package com.ecaservice.configuation;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.mapping.options.AdaBoostMapper;
import com.ecaservice.mapping.options.AdaBoostMapperImpl;
import com.ecaservice.mapping.options.AdaBoostOptionsMapper;
import com.ecaservice.mapping.options.AdaBoostOptionsMapperImpl;
import com.ecaservice.mapping.options.DecisionTreeFactory;
import com.ecaservice.mapping.options.DecisionTreeMapper;
import com.ecaservice.mapping.options.DecisionTreeMapperImpl;
import com.ecaservice.mapping.options.DecisionTreeOptionsMapper;
import com.ecaservice.mapping.options.DecisionTreeOptionsMapperImpl;
import com.ecaservice.mapping.options.ExtraTreesMapper;
import com.ecaservice.mapping.options.ExtraTreesMapperImpl;
import com.ecaservice.mapping.options.ExtraTreesOptionsMapper;
import com.ecaservice.mapping.options.ExtraTreesOptionsMapperImpl;
import com.ecaservice.mapping.options.HeterogeneousClassifierFactory;
import com.ecaservice.mapping.options.HeterogeneousClassifierMapper;
import com.ecaservice.mapping.options.HeterogeneousClassifierMapperImpl;
import com.ecaservice.mapping.options.HeterogeneousClassifierOptionsMapper;
import com.ecaservice.mapping.options.HeterogeneousClassifierOptionsMapperImpl;
import com.ecaservice.mapping.options.J48Mapper;
import com.ecaservice.mapping.options.J48MapperImpl;
import com.ecaservice.mapping.options.J48OptionsMapper;
import com.ecaservice.mapping.options.J48OptionsMapperImpl;
import com.ecaservice.mapping.options.KNearestNeighboursMapper;
import com.ecaservice.mapping.options.KNearestNeighboursMapperImpl;
import com.ecaservice.mapping.options.KNearestNeighboursOptionsMapper;
import com.ecaservice.mapping.options.KNearestNeighboursOptionsMapperImpl;
import com.ecaservice.mapping.options.LogisticMapper;
import com.ecaservice.mapping.options.LogisticMapperImpl;
import com.ecaservice.mapping.options.LogisticOptionsMapper;
import com.ecaservice.mapping.options.LogisticOptionsMapperImpl;
import com.ecaservice.mapping.options.NeuralNetworkMapper;
import com.ecaservice.mapping.options.NeuralNetworkMapperImpl;
import com.ecaservice.mapping.options.NeuralNetworkOptionsMapper;
import com.ecaservice.mapping.options.NeuralNetworkOptionsMapperImpl;
import com.ecaservice.mapping.options.RandomForestsMapper;
import com.ecaservice.mapping.options.RandomForestsMapperImpl;
import com.ecaservice.mapping.options.RandomForestsOptionsMapper;
import com.ecaservice.mapping.options.RandomForestsOptionsMapperImpl;
import com.ecaservice.mapping.options.RandomNetworkOptionsMapper;
import com.ecaservice.mapping.options.RandomNetworkOptionsMapperImpl;
import com.ecaservice.mapping.options.RandomNetworksMapper;
import com.ecaservice.mapping.options.RandomNetworksMapperImpl;
import com.ecaservice.mapping.options.StackingClassifierMapper;
import com.ecaservice.mapping.options.StackingClassifierMapperImpl;
import com.ecaservice.mapping.options.StackingOptionsMapper;
import com.ecaservice.mapping.options.StackingOptionsMapperImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Classifier options mapper configuration.
 *
 * @author Roman Batygin
 */
@TestConfiguration
public class ClassifierOptionsMapperConfiguration {

    @Bean
    public AdaBoostOptionsMapper adaBoostOptionsMapper() {
        return new AdaBoostOptionsMapperImpl();
    }

    @Bean
    public HeterogeneousClassifierOptionsMapper heterogeneousClassifierOptionsMapper(
            HeterogeneousClassifierFactory heterogeneousClassifierFactory) {
        return new HeterogeneousClassifierOptionsMapperImpl(heterogeneousClassifierFactory);
    }

    @Bean
    public StackingOptionsMapper stackingOptionsMapper() {
        return new StackingOptionsMapperImpl();
    }

    @Bean
    public ExtraTreesOptionsMapper extraTreesOptionsMapper() {
        return new ExtraTreesOptionsMapperImpl();
    }

    @Bean
    public RandomForestsOptionsMapper randomForestsOptionsMapper() {
        return new RandomForestsOptionsMapperImpl();
    }

    @Bean
    public RandomNetworkOptionsMapper randomNetworkOptionsMapper() {
        return new RandomNetworkOptionsMapperImpl();
    }

    @Bean
    public DecisionTreeOptionsMapper decisionTreeOptionsMapper(DecisionTreeFactory decisionTreeFactory) {
        return new DecisionTreeOptionsMapperImpl(decisionTreeFactory);
    }

    @Bean
    public KNearestNeighboursOptionsMapper kNearestNeighboursOptionsMapper() {
        return new KNearestNeighboursOptionsMapperImpl();
    }

    @Bean
    public NeuralNetworkOptionsMapper neuralNetworkOptionsMapper() {
        return new NeuralNetworkOptionsMapperImpl();
    }

    @Bean
    public J48OptionsMapper j48OptionsMapper() {
        return new J48OptionsMapperImpl();
    }

    @Bean
    public LogisticOptionsMapper logisticOptionsMapper() {
        return new LogisticOptionsMapperImpl();
    }

    @Bean
    public ExperimentConfig experimentConfig() {
        return new ExperimentConfig();
    }

    @Bean
    public DecisionTreeFactory decisionTreeFactory() {
        return new DecisionTreeFactory();
    }

    @Bean
    public HeterogeneousClassifierFactory heterogeneousClassifierFactory() {
        return new HeterogeneousClassifierFactory();
    }

    @Bean
    public AdaBoostMapper adaBoostMapper() {
        return new AdaBoostMapperImpl();
    }

    @Bean
    public HeterogeneousClassifierMapper heterogeneousClassifierMapper() {
        return new HeterogeneousClassifierMapperImpl();
    }

    @Bean
    public ExtraTreesMapper extraTreesMapper() {
        return new ExtraTreesMapperImpl();
    }

    @Bean
    public StackingClassifierMapper stackingClassifierMapper() {
        return new StackingClassifierMapperImpl();
    }

    @Bean
    public RandomForestsMapper randomForestsMapper() {
        return new RandomForestsMapperImpl();
    }

    @Bean
    public RandomNetworksMapper randomNetworksMapper() {
        return new RandomNetworksMapperImpl();
    }

    @Bean
    public DecisionTreeMapper decisionTreeMapper() {
        return new DecisionTreeMapperImpl();
    }

    @Bean
    public KNearestNeighboursMapper kNearestNeighboursMapper() {
        return new KNearestNeighboursMapperImpl();
    }

    @Bean
    public NeuralNetworkMapper neuralNetworkMapper() {
        return new NeuralNetworkMapperImpl();
    }

    @Bean
    public J48Mapper j48Mapper() {
        return new J48MapperImpl();
    }

    @Bean
    public LogisticMapper logisticMapper() {
        return new LogisticMapperImpl();
    }
}
