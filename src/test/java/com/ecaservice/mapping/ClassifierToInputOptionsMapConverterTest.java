package com.ecaservice.mapping;

import com.ecaservice.model.InputOptionsMap;
import eca.metrics.KNearestNeighbours;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests that checks ClassifierToInputOptionsListConverter functionality
 * (see {@link ClassifierToInputOptionsMapConverter}).
 *
 * @author Roman Batygin
 */
public class ClassifierToInputOptionsMapConverterTest extends AbstractConverterTest {

    @Test
    public void testMapClassifierTOInputOptionsList() {
        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
        Map<String, String> inputOptionsMap = mapper.map(kNearestNeighbours, InputOptionsMap.class)
                .getInputOptionsMap();
        String[] options = kNearestNeighbours.getOptions();
        assertEquals(inputOptionsMap.size(), options.length / 2);

        for (int i = 0; i < options.length; i += 2) {
            assertEquals(options[i + 1], inputOptionsMap.get(options[i]));
        }

    }
}
