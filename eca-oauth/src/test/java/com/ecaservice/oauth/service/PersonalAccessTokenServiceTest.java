package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.dto.CreatePersonalAccessTokenDto;
import com.ecaservice.oauth.entity.PersonalAccessTokenEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.DuplicatePersonalAccessTokenNameException;
import com.ecaservice.oauth.mapping.PersonalAccessTokenMapperImpl;
import com.ecaservice.oauth.repository.PersonalAccessTokenRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.user.dto.PersonalAccessTokenType;
import com.ecaservice.web.dto.model.PersonalAccessTokenDetailsDto;
import com.ecaservice.web.dto.model.SimplePageRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link PersonalAccessTokenService} functionality.
 *
 * @author Roman Batygin
 */
@Import({PersonalAccessTokenService.class, PersonalAccessTokenMapperImpl.class})
class PersonalAccessTokenServiceTest extends AbstractJpaTest {

    private static final String PASSWORD = "pa66word!";
    private static final int TOKEN_VALIDITY_MONTH = 3;
    private static final String TOKEN_NAME = "TokenName";
    private static final String INVALID_USER = "abc";
    private static final int PAGE = 0;
    private static final int PAGE_SIZE = 10;
    private static final String INVALID_TOKEN = "invalid_token";

    @Autowired
    private UserEntityRepository userEntityRepository;
    @Autowired
    private PersonalAccessTokenRepository personalAccessTokenRepository;

    @Autowired
    private PersonalAccessTokenService personalAccessTokenService;

    private PasswordEncoder passwordEncoder;

    private UserEntity userEntity;

    @Override
    public void init() {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        userEntity = createAndSaveUser();
    }

    @Override
    public void deleteAll() {
        personalAccessTokenRepository.deleteAll();
        userEntityRepository.deleteAll();
    }

    @Test
    void testCreatePersonalAccessToken() {
        createToken();
        var tokens = personalAccessTokenRepository.findAll();
        assertThat(tokens.size()).isOne();
        PersonalAccessTokenEntity personalAccessTokenEntity = tokens.getFirst();
        assertThat(personalAccessTokenEntity.getName()).isEqualTo(TOKEN_NAME);
        assertThat(personalAccessTokenEntity.getUserEntity().getLogin()).isEqualTo(userEntity.getLogin());
        assertThat(personalAccessTokenEntity.getTokenType()).isEqualTo(PersonalAccessTokenType.USER_TOKEN);
    }

    @Test
    void testCreatePersonalAccessTokenShouldThrowDuplicatePersonalAccessTokenNameException() {
        createToken();
        assertThrows(DuplicatePersonalAccessTokenNameException.class, this::createToken);
    }

    @Test
    void testCreatePersonalAccessTokenForNotExistingUser() {
        CreatePersonalAccessTokenDto createPersonalAccessTokenDto = new CreatePersonalAccessTokenDto();
        assertThrows(EntityNotFoundException.class,
                () -> personalAccessTokenService.createToken(INVALID_USER, createPersonalAccessTokenDto));
    }

    @Test
    void testDeleteToken() {
        var personalAccessTokenDetailsDto = createToken();
        personalAccessTokenService.removeToken(personalAccessTokenDetailsDto.getId());
        assertThat(personalAccessTokenRepository.count()).isZero();
    }

    @Test
    void testDeleteTokenNotExistingToken() {
        assertThrows(EntityNotFoundException.class,
                () -> personalAccessTokenService.removeToken(-1L));
    }

    @Test
    void testGetTokensPage() {
        var personalAccessTokenDetailsDto = createToken();
        var tokensPage = personalAccessTokenService.getNextPage(new SimplePageRequestDto(PAGE, PAGE_SIZE),
                userEntity.getLogin());
        assertThat(tokensPage).isNotNull();
        assertThat(tokensPage.getTotalCount()).isOne();
        assertThat(tokensPage.getContent()).isNotNull();
        assertThat(tokensPage.getContent().size()).isOne();
        assertThat(tokensPage.getContent().getFirst().getId()).isEqualTo(personalAccessTokenDetailsDto.getId());
    }

    @Test
    void testTokenIsValid() {
        var personalAccessTokenDetailsDto = createToken();
        var tokenInfo = personalAccessTokenService.verifyToken(personalAccessTokenDetailsDto.getToken());
        assertThat(tokenInfo).isNotNull();
        assertThat(tokenInfo.isValid()).isTrue();
    }

    @Test
    void testTokenIsNotValid() {
        var tokenInfo = personalAccessTokenService.verifyToken(INVALID_TOKEN);
        assertThat(tokenInfo).isNotNull();
        assertThat(tokenInfo.isValid()).isFalse();
    }

    @Test
    void testTokenIsExpired() {
        var personalAccessTokenDetailsDto = createToken();
        var personalAccessTokenEntity =
                personalAccessTokenRepository.findById(personalAccessTokenDetailsDto.getId()).orElse(null);
        assertThat(personalAccessTokenEntity).isNotNull();
        personalAccessTokenEntity.setExpireDate(LocalDateTime.now().minusMinutes(1L));
        personalAccessTokenRepository.save(personalAccessTokenEntity);
        var tokenInfo = personalAccessTokenService.verifyToken(personalAccessTokenDetailsDto.getToken());
        assertThat(tokenInfo).isNotNull();
        assertThat(tokenInfo.isValid()).isFalse();
    }

    private PersonalAccessTokenDetailsDto createToken() {
        CreatePersonalAccessTokenDto createPersonalAccessTokenDto = new CreatePersonalAccessTokenDto();
        createPersonalAccessTokenDto.setName(TOKEN_NAME);
        createPersonalAccessTokenDto.setTokenType(PersonalAccessTokenType.USER_TOKEN);
        createPersonalAccessTokenDto.setExpirationMonth(TOKEN_VALIDITY_MONTH);
        return personalAccessTokenService.createToken(userEntity.getLogin(), createPersonalAccessTokenDto);
    }

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setPassword(passwordEncoder.encode(PASSWORD));
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }
}
