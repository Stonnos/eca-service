package com.ecaservice.oauth.service.tfa;

import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.entity.TfaCodeEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.model.TfaCodeModel;
import com.ecaservice.oauth.repository.TfaCodeRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.security.model.TfaCodeAuthenticationRequest;
import com.ecaservice.oauth.service.AuthenticationJsonSerializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.oauth.security.OAuth2AdditionalErrorCodes.INVALID_TFA_CODE;
import static com.ecaservice.oauth.security.OAuth2AdditionalErrorCodes.TFA_CODE_EXPIRED;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * TFA code service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TfaCodeService {

    private final TfaConfig tfaConfig;
    private final AuthenticationJsonSerializer authenticationJsonSerializer;
    private final UserEntityRepository userEntityRepository;
    private final TfaCodeRepository tfaCodeRepository;

    /**
     * Creates 2fa authorization code.
     *
     * @param tfaCodeAuthenticationRequest - tfa code authentication request
     * @return 2fa code model
     */
    @Transactional
    public TfaCodeModel createAuthorizationCode(TfaCodeAuthenticationRequest tfaCodeAuthenticationRequest) {
        String user = tfaCodeAuthenticationRequest.getAuthentication().getName();
        var userEntity = userEntityRepository.findUser(user);
        if (userEntity == null) {
            throw new UsernameNotFoundException(String.format("User with login [%s] doesn't exists!", user));
        }
        //Invalidate previous code
        invalidatePreviousCodes(userEntity);
        String token = UUID.randomUUID().toString();
        String code = RandomStringUtils.random(tfaConfig.getCodeLength(), false, true);
        saveNewCode(token, code, userEntity, tfaCodeAuthenticationRequest);
        return TfaCodeModel.builder()
                .token(token)
                .code(code)
                .build();
    }

    /**
     * Consumes 2fa authorization code.
     *
     * @param token - token value
     * @param code  - authorization code
     * @return tfa code authentication request
     */
    public TfaCodeAuthenticationRequest consumeAuthorizationCode(String token, String code) {
        var tfaCodeEntity = tfaCodeRepository.findByToken(md5Hex(token));
        if (tfaCodeEntity == null) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_TOKEN);
        }
        String md5Code = md5Hex(code);
        if (!tfaCodeEntity.getCode().equals(md5Code)) {
            throw new OAuth2AuthenticationException(INVALID_TFA_CODE);
        }
        if (LocalDateTime.now().isAfter(tfaCodeEntity.getExpireDate())) {
            throw new OAuth2AuthenticationException(TFA_CODE_EXPIRED);
        }
        Authentication authentication = authenticationJsonSerializer.deserialize(tfaCodeEntity.getAuthentication());
        tfaCodeRepository.delete(tfaCodeEntity);
        return new TfaCodeAuthenticationRequest(tfaCodeEntity.getRegisteredClientId(), authentication);
    }

    private void invalidatePreviousCodes(UserEntity userEntity) {
        tfaCodeRepository.deleteActiveCodes(userEntity, LocalDateTime.now());
    }

    private void saveNewCode(String token, String code, UserEntity userEntity,
                             TfaCodeAuthenticationRequest tfaCodeAuthenticationRequest) {
        var tfaCodeEntity = new TfaCodeEntity();
        tfaCodeEntity.setToken(md5Hex(token));
        tfaCodeEntity.setCode(md5Hex(code));
        tfaCodeEntity.setRegisteredClientId(tfaCodeAuthenticationRequest.getClientId());
        tfaCodeEntity.setAuthentication(
                authenticationJsonSerializer.serialize(tfaCodeAuthenticationRequest.getAuthentication()));
        tfaCodeEntity.setExpireDate(LocalDateTime.now().plusSeconds(tfaConfig.getCodeValiditySeconds()));
        tfaCodeEntity.setUserEntity(userEntity);
        tfaCodeEntity.setCreated(LocalDateTime.now());
        tfaCodeRepository.save(tfaCodeEntity);
        log.info("New tfa code has been generated for user [{}]", userEntity.getId());
    }
}
