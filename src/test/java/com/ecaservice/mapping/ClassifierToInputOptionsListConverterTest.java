package com.ecaservice.mapping;

import com.ecaservice.model.InputOptionsList;
import com.ecaservice.model.entity.InputOptions;
import eca.metrics.KNearestNeighbours;
import eca.metrics.distances.ChebyshevDistance;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests that checks ClassifierToInputOptionsListConverter functionality
 * (see {@link ClassifierToInputOptionsListConverter}).
 *
 * @author Roman Batygin
 */
public class ClassifierToInputOptionsListConverterTest extends AbstractConverterTest {

    @Test
    public void testMapClassifierTOInputOptionsList() {
        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
        kNearestNeighbours.setDistance(new ChebyshevDistance());
        kNearestNeighbours.setWeight(0.77);
        kNearestNeighbours.setNumNeighbours(50);

        List<InputOptions> optionsList = mapper.map(kNearestNeighbours, InputOptionsList.class)
                .getInputOptionsList();
        String[] options = kNearestNeighbours.getOptions();

        assertTrue(optionsList.size() == 3);

        for (int i = 0, j = 0; i < optionsList.size(); i++, j += 2) {
            InputOptions inputOptions = optionsList.get(i);
            assertEquals(inputOptions.getName(), options[j]);
            assertEquals(inputOptions.getValue(), options[j + 1]);
        }
    }
}
