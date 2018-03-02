package com.ecaservice.util;

import com.ecaservice.exception.EcaServiceException;
import com.ecaservice.exception.ExperimentException;
import weka.classifiers.AbstractClassifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Classifier utility class.
 *
 * @author Roman Batygin
 */
public class ClassifierUtils {

    /**
     * Creates classifiers copies.
     *
     * @param classifiers classifiers list to copying
     * @return classifiers copies
     */
    public static List<AbstractClassifier> createCopies(List<AbstractClassifier> classifiers) {
        List<AbstractClassifier> copies = new ArrayList<>(classifiers.size());
        try {
            for (AbstractClassifier classifier : classifiers) {
                copies.add((AbstractClassifier) AbstractClassifier.makeCopy(classifier));
            }
        } catch (Exception ex) {
            throw new EcaServiceException(ex.getMessage());
        }
        return copies;
    }
}
