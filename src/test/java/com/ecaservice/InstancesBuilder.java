package com.ecaservice;

import eca.generators.SimpleDataGenerator;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */

public class InstancesBuilder {

    public static Instances generate(int numInstances, int numAttributes) {
        SimpleDataGenerator simpleDataGenerator = new SimpleDataGenerator();
        simpleDataGenerator.setNumInstances(numInstances);
        simpleDataGenerator.setNumAttributes(numAttributes);
        return simpleDataGenerator.generate();
    }
}
