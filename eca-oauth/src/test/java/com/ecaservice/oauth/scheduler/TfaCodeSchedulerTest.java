package com.ecaservice.oauth.scheduler;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.config.TfaConfig;
import com.ecaservice.oauth.entity.TfaCodeEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.TfaCodeRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TfaCodeScheduler} class.
 *
 * @author Roman Batygin
 */
@Import({TfaCodeScheduler.class, TfaConfig.class})
class TfaCodeSchedulerTest extends AbstractJpaTest {

    private static final int EXPIRED_CODES_NUM = 5;
    private static final long OFFSET_SECONDS = 5L;

    @Inject
    private UserEntityRepository userEntityRepository;
    @Inject
    private TfaCodeRepository tfaCodeRepository;

    @Inject
    private TfaCodeScheduler tfaCodeScheduler;

    @Override
    public void deleteAll() {
        tfaCodeRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    @Override
    public void init() {
        var userEntity = createAndSaveUser();
        IntStream.range(0, EXPIRED_CODES_NUM).forEach(
                i -> createAndSaveTfaCode(userEntity, LocalDateTime.now().minusSeconds(OFFSET_SECONDS))
        );
        createAndSaveTfaCode(userEntity, LocalDateTime.now().plusSeconds(OFFSET_SECONDS));
    }

    @Test
    void testDeleteExpiredCodes() {
        tfaCodeScheduler.deleteExpiredCodes();
        assertThat(tfaCodeRepository.count()).isOne();
    }

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }

    private void createAndSaveTfaCode(UserEntity userEntity, LocalDateTime expiredDate) {
        var tfaCodeEntity = new TfaCodeEntity();
        tfaCodeEntity.setUserEntity(userEntity);
        tfaCodeEntity.setToken(md5Hex(UUID.randomUUID().toString()));
        tfaCodeEntity.setCode(md5Hex(UUID.randomUUID().toString()));
        tfaCodeEntity.setAuthentication(new byte[0]);
        tfaCodeEntity.setExpireDate(expiredDate);
        tfaCodeRepository.save(tfaCodeEntity);
    }
}
