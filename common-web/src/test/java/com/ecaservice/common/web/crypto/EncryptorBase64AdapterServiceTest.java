package com.ecaservice.common.web.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.ecaservice.common.web.crypto.factory.EncryptFactory.getAesBytesEncryptor;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EncryptorBase64AdapterService} class.
 *
 * @author Roman Batygin
 */
class EncryptorBase64AdapterServiceTest {

    private static final String TEST_MESSAGE = "Test message";
    private static final String PASSWORD = "passW0rd!";
    private static final String SALT = "s@lt";

    private EncryptorBase64AdapterService encryptorBase64AdapterService;

    @BeforeEach
    void init() {
        var aesEncryptor = getAesBytesEncryptor(PASSWORD, SALT);
        encryptorBase64AdapterService = new EncryptorBase64AdapterService(aesEncryptor);
    }

    @Test
    void testEncryptMessage() {
        String cipher = encryptorBase64AdapterService.encrypt(TEST_MESSAGE);
        assertThat(cipher).isNotNull();
        String encoded = encryptorBase64AdapterService.decrypt(cipher);
        assertThat(encoded).isEqualTo(TEST_MESSAGE);
    }
}
