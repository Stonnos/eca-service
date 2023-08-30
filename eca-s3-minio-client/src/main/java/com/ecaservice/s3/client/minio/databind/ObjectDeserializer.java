package com.ecaservice.s3.client.minio.databind;

import java.io.IOException;
import java.io.InputStream;

/**
 * Object deserializer interface.
 *
 * @param <T> object generic type
 * @author Roman Batygin
 */
public interface ObjectDeserializer<T> {

    /**
     * Deserializes object.
     *
     * @param inputStream - input stream
     * @param targetClazz - object class
     * @return result object
     * @throws IOException            in case of I/O error
     * @throws ClassNotFoundException in case of class not found error
     */
    T deserialize(InputStream inputStream, Class<T> targetClazz) throws IOException, ClassNotFoundException;
}
