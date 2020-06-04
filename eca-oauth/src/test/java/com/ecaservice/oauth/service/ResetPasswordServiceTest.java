package com.ecaservice.oauth.service;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.config.ResetPasswordConfig;
import com.ecaservice.oauth.dto.ForgotPasswordRequest;
import com.ecaservice.oauth.entity.ResetPasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.ResetPasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Inject;
import java.util.Collections;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link ResetPasswordService} functionality.
 *
 * @author Roman Batygin
 */
@Import(ResetPasswordConfig.class)
class ResetPasswordServiceTest extends AbstractJpaTest {

    @Inject
    private ResetPasswordConfig resetPasswordConfig;
    @Inject
    private UserEntityRepository userEntityRepository;
    @Inject
    private ResetPasswordRequestRepository resetPasswordRequestRepository;

    private ResetPasswordService resetPasswordService;

    @Override
    public void init() {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        resetPasswordService =
                new ResetPasswordService(resetPasswordConfig, passwordEncoder, resetPasswordRequestRepository,
                        userEntityRepository);
    }

    @Override
    public void deleteAll() {
        resetPasswordRequestRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    @Test
    void testSaveNewResetPasswordRequest() {
        UserEntity userEntity = createUserEntity();
        userEntity.setRoles(Collections.emptySet());
        userEntityRepository.save(userEntity);
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(userEntity.getEmail());
        ResetPasswordRequestEntity resetPasswordRequestEntity =
                resetPasswordService.getOrSaveResetPasswordRequest(forgotPasswordRequest);
        assertThat(resetPasswordRequestEntity).isNotNull();
        assertThat(resetPasswordRequestEntity.getExpireDate()).isNotNull();
        assertThat(resetPasswordRequestEntity.getToken()).isNotNull();
        assertThat(resetPasswordRequestEntity.getUserEntity()).isNotNull();
        assertThat(resetPasswordRequestEntity.getResetDate()).isNull();
        assertThat(resetPasswordRequestRepository.count()).isOne();
    }

    @Test
    void testGetResetPasswordRequestFromCache() {
        UserEntity userEntity = createUserEntity();
        userEntity.setRoles(Collections.emptySet());
        userEntityRepository.save(userEntity);
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(userEntity.getEmail());
        resetPasswordService.getOrSaveResetPasswordRequest(forgotPasswordRequest);
        resetPasswordService.getOrSaveResetPasswordRequest(forgotPasswordRequest);
        assertThat(resetPasswordRequestRepository.count()).isOne();
    }

    @Test
    void testSaveResetPasswordRequestWithException() {
        UserEntity userEntity = createUserEntity();
        userEntity.setRoles(Collections.emptySet());
        userEntityRepository.save(userEntity);
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest(StringUtils.EMPTY);
        assertThrows(IllegalStateException.class,
                () -> resetPasswordService.getOrSaveResetPasswordRequest(forgotPasswordRequest));
    }
}
