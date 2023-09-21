package com.ecaservice.s3.client.minio.databind;

import java.io.IOException;

/**
 * Object serializer interface.
 *
 * @author Roman Batygin
 */
public interface ObjectSerializer {

    /**
     * Serializes object
     *
     * @param object - object
     * @return serialized object as bytes array
     * @throws IOException in case of I/O error
     */
    byte[] serialize(Object object) throws IOException;
}
