package com.ecaservice.external.api.test.util;

import eca.core.ModelSerializationHelper;
import eca.core.model.ClassificationModel;
import eca.data.file.resource.UrlResource;
import eca.dataminer.AbstractExperiment;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.net.URL;

/**
 * Classifier models utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ModelUtils {

    /**
     * Downloads classifier model.
     *
     * @param modelUrl - model url
     * @return classifier model
     * @throws IOException in case of I/O error
     */
    public static ClassificationModel downloadModel(String modelUrl) throws IOException {
        URL experimentUrl = new URL(modelUrl);
        return ModelSerializationHelper.deserialize(new UrlResource(experimentUrl), ClassificationModel.class);
    }

    /**
     * Downloads experiment model.
     *
     * @param modelUrl - model url
     * @return experiment model
     * @throws IOException in case of I/O error
     */
    public static AbstractExperiment<?> downloadExperiment(String modelUrl) throws IOException {
        URL experimentUrl = new URL(modelUrl);
        return ModelSerializationHelper.deserialize(new UrlResource(experimentUrl), AbstractExperiment.class);
    }
}
