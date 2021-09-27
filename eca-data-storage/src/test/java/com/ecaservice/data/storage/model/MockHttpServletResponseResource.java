package com.ecaservice.data.storage.model;

import eca.data.file.resource.DataResource;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Mock http servlet response resource model.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class MockHttpServletResponseResource implements DataResource<MockHttpServletResponse> {

    private final String fileName;
    private final MockHttpServletResponse resource;

    @Override
    public MockHttpServletResponse getResource() {
        return resource;
    }

    @Override
    public InputStream openInputStream() {
        return new ByteArrayInputStream(resource.getContentAsByteArray());
    }

    @Override
    public String getFile() {
        return fileName;
    }
}
