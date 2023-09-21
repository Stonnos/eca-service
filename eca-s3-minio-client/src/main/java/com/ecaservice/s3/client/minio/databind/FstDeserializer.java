package com.ecaservice.s3.client.minio.databind;

import lombok.Cleanup;
import org.nustaq.serialization.FSTObjectInput;

import java.io.IOException;
import java.io.InputStream;

/**
 * Fast java object deserializer.
 *
 * @param <T> object generic type
 * @author Roman Batygin
 */
public class FstDeserializer<T> implements ObjectDeserializer<T> {

    @Override
    public T deserialize(InputStream inputStream, Class<T> targetClazz) throws IOException, ClassNotFoundException {
        @Cleanup var in = new FSTObjectInput(inputStream);
        Object readObject = in.readObject();
        return targetClazz.cast(readObject);
    }
}
