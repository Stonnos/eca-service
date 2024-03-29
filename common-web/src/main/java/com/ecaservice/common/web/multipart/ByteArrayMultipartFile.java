package com.ecaservice.common.web.multipart;

import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Byte array multipart file.
 */
@AllArgsConstructor
public class ByteArrayMultipartFile implements MultipartFile {

    private String name;

    private String originalFilename;

    private byte[] bytes;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return bytes.length == 0;
    }

    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(File destination) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(destination)) {
            outputStream.write(bytes);
        }
    }
}
