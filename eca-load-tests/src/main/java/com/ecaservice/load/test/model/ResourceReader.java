package com.ecaservice.load.test.model;

import eca.data.file.resource.AbstractResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Resource reader.
 *
 * @author Roman Batygin
 */
public class ResourceReader extends AbstractResource<Resource> {

    /**
     * Creates resource reader.
     *
     * @param resource - resource object
     */
    public ResourceReader(Resource resource) {
        super(resource);
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return getResource().getInputStream();
    }

    @Override
    public String getFile() {
        return getResource().getFilename();
    }
}
