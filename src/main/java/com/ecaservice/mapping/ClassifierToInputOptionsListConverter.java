package com.ecaservice.mapping;

import com.ecaservice.model.InputOptionsList;
import com.ecaservice.model.entity.InputOptions;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import org.springframework.stereotype.Component;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the conversion of classifier input options array into the list.
 *
 * @author Roman Batygin
 */
@Component
public class ClassifierToInputOptionsListConverter extends CustomConverter<Classifier, InputOptionsList> {

    @Override
    public InputOptionsList convert(Classifier classifier, Type<? extends InputOptionsList> listType) {
        String[] options = ((AbstractClassifier) classifier).getOptions();

        List<InputOptions> optionsList = new ArrayList<>(options.length / 2);

        for (int i = 0; i < options.length; i += 2) {
            InputOptions inputOptions = new InputOptions();
            inputOptions.setName(options[i]);
            inputOptions.setValue(options[i + 1]);
            optionsList.add(inputOptions);
        }

        return new InputOptionsList(optionsList);
    }
}
