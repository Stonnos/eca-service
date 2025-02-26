package com.ecaservice.s3.client.minio.databind;

import lombok.Cleanup;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Zip object deserializer.
 *
 * @param <T> object generic type
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class ZipDeserializerDecorator<T> implements ObjectDeserializer<T> {

    private final ObjectDeserializer<T> deserializer;

    @Override
    public T deserialize(InputStream inputStream, Class<T> targetClazz) throws IOException, ClassNotFoundException {
        @Cleanup ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        Objects.requireNonNull(zipEntry, "Expected model file in zip archive");
        return deserializer.deserialize(zipInputStream, targetClazz);
    }
}
