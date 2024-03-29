package com.ecaservice.data.storage.model;

import eca.data.file.resource.AbstractResource;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Multipart file resource.
 *
 * @author Roman Batygin
 */
public class MultipartFileResource extends AbstractResource<MultipartFile> {

    /**
     * Creates multipart file resource.
     *
     * @param resource - multipart file resource
     */
    public MultipartFileResource(MultipartFile resource) {
        super(resource);
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return getResource().getInputStream();
    }

    @Override
    public String getFile() {
        return getResource().getOriginalFilename();
    }

    @Override
    public String getExtension() {
        return FilenameUtils.getExtension(getFile());
    }
}
