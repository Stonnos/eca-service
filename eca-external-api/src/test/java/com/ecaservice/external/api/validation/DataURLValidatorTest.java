package com.ecaservice.external.api.validation;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link DataURLValidator} functionality.
 *
 * @author Roman Batygin
 */
class DataURLValidatorTest {

    private static final String DATA_URL = "data://train";
    private static final String INVALID_URL = "xyz://dsf.xls";
    private static final String FTP_URL = "ftp://some-data.xls";
    private static final String HTTP_URL = "http://some-data.xls";

    private DataURLValidator dataURLValidator = new DataURLValidator();

    @Test
    void testEmptyUrl() {
        assertThat(dataURLValidator.isValid(StringUtils.EMPTY, null)).isFalse();
    }

    @Test
    void testValidDataUrl() {
        assertThat(dataURLValidator.isValid(DATA_URL, null)).isTrue();
    }

    @Test
    void testInvalidUrl() {
        assertThat(dataURLValidator.isValid(INVALID_URL, null)).isFalse();
    }

    @Test
    void testValidHttpUrl() {
        assertThat(dataURLValidator.isValid(HTTP_URL, null)).isTrue();
    }

    @Test
    void testValidFtpUrl() {
        assertThat(dataURLValidator.isValid(FTP_URL, null)).isTrue();
    }
}
