package com.ecaservice.converter;

import com.ecaservice.model.entity.InputOptions;
import org.springframework.stereotype.Component;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.CustomConverter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the conversion of classifier input options array into the list.
 * @author Roman Batygin
 */
@Component
public class InputOptionsConverterImpl extends CustomConverter<String[], List<InputOptions>>  {

    @Override
    public List<InputOptions> convert(String[] options, Type<? extends List<InputOptions>> listType) {
        List<InputOptions> optionsList = new ArrayList<>( options.length / 2);

        for (int i = 0; i < options.length; i += 2) {
            InputOptions inputOptions = new InputOptions();
            inputOptions.setName(options[i]);
            inputOptions.setValue(options[i + 1]);
            optionsList.add(inputOptions);
        }

        return optionsList;
    }
}
