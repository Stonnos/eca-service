package com.ecaservice.service;

import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.repository.EvaluationLogRepository;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.HeterogeneousClassifier;
import eca.neural.NeuralNetwork;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.util.Utils;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import weka.classifiers.AbstractClassifier;

import javax.inject.Inject;

/**
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClassifierInputOptionsServiceTest {

    @Inject
    private EvaluationLogRepository evaluationLogRepository;

    @Inject
    private ClassifierInputOptionsService classifierInputOptionsService;


    @Test
    public void testDecisionTree() throws Exception {
        EvaluationLog evaluationLog = new EvaluationLog();
        ClassifiersSet classifiers = new ClassifiersSet();
        classifiers.addClassifier(new CART());
        classifiers.addClassifier(new CART());
        AbstractClassifier classifier = new HeterogeneousClassifier(classifiers);
        evaluationLog.setClassifierName(classifier.getClass().getSimpleName());
        evaluationLog.setInputOptionsMap(Utils.getClassifierInputOptionsMap(classifier));
        evaluationLog.getInputOptionsMap().put("Начальное значение (Se32ed)", "2323");
        evaluationLogRepository.save(evaluationLog);
        classifierInputOptionsService.migrateInputOptions();
        EvaluationLog actual = evaluationLogRepository.findAll().get(0);
       // Assertions.assertThat(actual.getClassifierInputOptions().size()).isEqualTo(evaluationLog
        //        .getEvaluationOptionsMap().size());
    }
}
