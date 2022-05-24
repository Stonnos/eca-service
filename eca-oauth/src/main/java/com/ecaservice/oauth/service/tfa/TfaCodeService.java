package com.ecaservice.oauth.service.tfa;

import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.entity.TfaCodeEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.TfaCodeRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * TFA code service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TfaCodeService implements AuthorizationCodeServices {

    private final TfaConfig tfaConfig;
    private final UserEntityRepository userEntityRepository;
    private final TfaCodeRepository tfaCodeRepository;

    private RandomValueStringGenerator generator = new RandomValueStringGenerator();

    /**
     * Initialization method
     */
    @PostConstruct
    public void initialize() {
        this.generator.setLength(tfaConfig.getCodeLength());
    }

    @Override
    @Transactional
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String user = authentication.getName();
        var userEntity = userEntityRepository.findUser(user);
        if (userEntity == null) {
            throw new UsernameNotFoundException(String.format("User with login [%s] doesn't exists!", user));
        }
        //Invalidate previous code
        invalidatePreviousCodes(userEntity);
        return generateAndSaveNewCode(userEntity, authentication);
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) {
        var tfaCodeEntity = tfaCodeRepository.findByTokenAndExpireDateAfter(md5Hex(code), LocalDateTime.now());
        if (tfaCodeEntity == null) {
            throw new InvalidGrantException(String.format("Invalid authorization code: %s", code));
        }
        OAuth2Authentication authentication = SerializationUtils.deserialize(tfaCodeEntity.getAuthentication());
        tfaCodeRepository.delete(tfaCodeEntity);
        return authentication;
    }

    private void invalidatePreviousCodes(UserEntity userEntity) {
        var previousCodes = tfaCodeRepository.findActiveCodes(userEntity, LocalDateTime.now());
        if (!CollectionUtils.isEmpty(previousCodes)) {
            log.info("Found [{}] active tfa codes for user [{}]", previousCodes.size(), userEntity.getId());
            tfaCodeRepository.deleteAll(previousCodes);
            log.info("[{}] active tfa codes has been invalidated for user [{}]", previousCodes.size(),
                    userEntity.getId());
        }
    }

    private String generateAndSaveNewCode(UserEntity userEntity, OAuth2Authentication authentication) {
        String code = generator.generate();
        var tfaCodeEntity = new TfaCodeEntity();
        tfaCodeEntity.setToken(md5Hex(code));
        tfaCodeEntity.setAuthentication(SerializationUtils.serialize(authentication));
        tfaCodeEntity.setExpireDate(LocalDateTime.now().plusSeconds(tfaConfig.getCodeValiditySeconds()));
        tfaCodeEntity.setUserEntity(userEntity);
        tfaCodeRepository.save(tfaCodeEntity);
        log.info("New tfa code has been generated for user [{}]", userEntity.getId());
        return code;
    }
}
