package com.ecaservice.mapping;

import com.ecaservice.model.InputOptionsMap;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;
import weka.classifiers.AbstractClassifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements the conversion of classifier input options array into the list.
 *
 * @author Roman Batygin
 */
@Component
public class ClassifierToInputOptionsMapConverter extends CustomConverter<AbstractClassifier, InputOptionsMap> {

    @Override
    public InputOptionsMap convert(AbstractClassifier classifier, Type<? extends InputOptionsMap> listType) {
        String[] options = classifier.getOptions();
        Map<String, String> optionsMap = new HashMap<>();
        for (int i = 0; i < options.length; i += 2) {
            optionsMap.put(options[i], options[i + 1]);
        }
        return new InputOptionsMap(optionsMap);
    }
}
