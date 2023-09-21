package com.ecaservice.s3.client.minio.databind;

import lombok.Cleanup;
import org.nustaq.serialization.FSTObjectOutput;

import java.io.IOException;

/**
 * Fast java object serializer.
 *
 * @author Roman Batygin
 */
public class FstSerializer implements ObjectSerializer {

    @Override
    public byte[] serialize(Object object) throws IOException {
        @Cleanup var fstObjectOutput = new FSTObjectOutput();
        fstObjectOutput.writeObject(object);
        return fstObjectOutput.getBuffer();
    }
}
