package com.ecaservice.oauth.service.tfa;

import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.entity.TfaCodeEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.model.TfaCodeModel;
import com.ecaservice.oauth.repository.TfaCodeRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.oauth.service.SerializationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.common.web.util.MaskUtils.mask;
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
    private final SerializationHelper serializationHelper;
    private final UserEntityRepository userEntityRepository;
    private final TfaCodeRepository tfaCodeRepository;
    private final RandomValueStringGenerator generator = new RandomValueStringGenerator();

    /**
     * Initialization method
     */
    @PostConstruct
    public void initialize() {
        this.generator.setLength(tfaConfig.getCodeLength());
    }

    @Transactional
    public TfaCodeModel createAuthorizationCode(OAuth2Authentication authentication) {
        String user = authentication.getName();
        var userEntity = userEntityRepository.findUser(user);
        if (userEntity == null) {
            throw new UsernameNotFoundException(String.format("User with login [%s] doesn't exists!", user));
        }
        //Invalidate previous code
        invalidatePreviousCodes(userEntity);
        String token = UUID.randomUUID().toString();
        String code = generator.generate();
        saveNewCode(token, code, userEntity, authentication);
        return TfaCodeModel.builder()
                .token(token)
                .code(code)
                .build();
    }

    public OAuth2Authentication consumeAuthorizationCode(String token, String code) {
        var tfaCodeEntity = tfaCodeRepository.findByToken(md5Hex(token));
        if (tfaCodeEntity == null) {
            throw new InvalidGrantException(String.format("Invalid token: %s", mask(token)));
        }
        String md5Code = md5Hex(code);
        if (!tfaCodeEntity.getCode().equals(md5Code)) {
            throw new InvalidGrantException(String.format("Invalid code: %s", mask(code)));
        }
        if (LocalDateTime.now().isAfter(tfaCodeEntity.getExpireDate())) {
            throw new InvalidGrantException(String.format("Code %s has been expired", mask(code)));
        }
        OAuth2Authentication authentication = serializationHelper.deserialize(tfaCodeEntity.getAuthentication());
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

    private void saveNewCode(String token, String code, UserEntity userEntity, OAuth2Authentication authentication) {
        var tfaCodeEntity = new TfaCodeEntity();
        tfaCodeEntity.setToken(md5Hex(token));
        tfaCodeEntity.setCode(md5Hex(code));
        tfaCodeEntity.setAuthentication(serializationHelper.serialize(authentication));
        tfaCodeEntity.setExpireDate(LocalDateTime.now().plusSeconds(tfaConfig.getCodeValiditySeconds()));
        tfaCodeEntity.setUserEntity(userEntity);
        tfaCodeRepository.save(tfaCodeEntity);
        log.info("New tfa code has been generated for user [{}]", userEntity.getId());
    }
}
