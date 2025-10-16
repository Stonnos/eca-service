package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.audit.annotation.Audit;
import com.ecaservice.oauth.dto.CreatePersonalAccessTokenDto;
import com.ecaservice.oauth.entity.PersonalAccessTokenEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.DuplicatePersonalAccessTokenNameException;
import com.ecaservice.oauth.mapping.PersonalAccessTokenMapper;
import com.ecaservice.oauth.repository.PersonalAccessTokenRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.user.dto.PersonalAccessTokenInfoDto;
import com.ecaservice.user.dto.PersonalAccessTokenType;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PersonalAccessTokenDetailsDto;
import com.ecaservice.web.dto.model.PersonalAccessTokenDto;
import com.ecaservice.web.dto.model.SimplePageRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;

import static com.ecaservice.oauth.config.audit.AuditCodes.CREATE_PERSONAL_ACCESS_TOKEN;
import static com.ecaservice.oauth.config.audit.AuditCodes.DELETE_PERSONAL_ACCESS_TOKEN;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Personal access token service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalAccessTokenService {

    private static final int TOKEN_LENGTH = 96;

    private final StringKeyGenerator accessTokenGenerator =
            new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), TOKEN_LENGTH);

    private final PersonalAccessTokenMapper personalAccessTokenMapper;
    private final PersonalAccessTokenRepository personalAccessTokenRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * Creates personal access token.
     *
     * @param user                         - user login
     * @param createPersonalAccessTokenDto - personal access token data
     * @return generated personal access token data
     */
    @Audit(CREATE_PERSONAL_ACCESS_TOKEN)
    public PersonalAccessTokenDetailsDto createToken(String user,
                                                     CreatePersonalAccessTokenDto createPersonalAccessTokenDto) {
        log.info("Starting to create personal access token [{}] for user [{}]", createPersonalAccessTokenDto.getName(),
                user);
        UserEntity userEntity = getUser(user);
        if (personalAccessTokenRepository.existsByUserEntityAndName(userEntity,
                createPersonalAccessTokenDto.getName())) {
            throw new DuplicatePersonalAccessTokenNameException();
        }
        String token = accessTokenGenerator.generateKey();
        var personalAccessTokenEntity = saveToken(createPersonalAccessTokenDto, userEntity, token);
        var personalAccessTokenDetailsDto = new PersonalAccessTokenDetailsDto();
        personalAccessTokenDetailsDto.setId(personalAccessTokenEntity.getId());
        personalAccessTokenDetailsDto.setName(personalAccessTokenEntity.getName());
        personalAccessTokenDetailsDto.setValid(true);
        personalAccessTokenDetailsDto.setExpireDate(personalAccessTokenEntity.getExpireDate());
        personalAccessTokenDetailsDto.setToken(token);
        log.info("Personal access token [{}] has been created for user [{}]", createPersonalAccessTokenDto.getName(),
                user);
        return personalAccessTokenDetailsDto;
    }

    /**
     * Removes personal access token.
     *
     * @param id - token id
     * @return deleted personal access token entity
     */
    @Audit(DELETE_PERSONAL_ACCESS_TOKEN)
    public PersonalAccessTokenEntity removeToken(long id) {
        log.info("Starting to delete personal access token [{}]", id);
        var personalAccessTokenEntity = personalAccessTokenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PersonalAccessTokenEntity.class, id));
        personalAccessTokenRepository.delete(personalAccessTokenEntity);
        log.info("Personal access token [{}] has been deleted", id);
        return personalAccessTokenEntity;
    }

    /**
     * Gets current user personal access tokens next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @param user           - user login
     * @return personal access tokens page
     */
    public PageDto<PersonalAccessTokenDto> getNextPage(SimplePageRequestDto pageRequestDto, String user) {
        log.info("Gets user [{}] personal access tokens next page: {}", user, pageRequestDto);
        UserEntity userEntity = getUser(user);
        var pageRequest = PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize());
        var personalAccessTokensPage = personalAccessTokenRepository.findByUserEntity(userEntity, pageRequest);
        log.info(
                "User [{}] personal access tokens page [{} of {}] with size [{}] has been fetched for page request [{}]",
                user, personalAccessTokensPage.getNumber(), personalAccessTokensPage.getTotalPages(),
                personalAccessTokensPage.getNumberOfElements(), pageRequestDto);
        var personalAccessTokensDtoList = personalAccessTokenMapper.map(personalAccessTokensPage.getContent());
        return PageDto.of(personalAccessTokensDtoList, pageRequestDto.getPage(),
                personalAccessTokensPage.getTotalElements());
    }

    /**
     * Verify personal access token.
     *
     * @param token - token value
     * @return personal access token info dto
     */
    public PersonalAccessTokenInfoDto verifyToken(String token) {
        log.debug("Verify personal access token");
        String md5HashToken = md5Hex(token);
        PersonalAccessTokenInfoDto personalAccessTokenInfoDto = new PersonalAccessTokenInfoDto();
        var personalAccessTokenEntity = personalAccessTokenRepository.findByToken(md5HashToken);
        if (personalAccessTokenEntity == null) {
            personalAccessTokenInfoDto.setValid(false);
        } else {
            personalAccessTokenInfoDto.setTokenType(personalAccessTokenEntity.getTokenType());
            if (PersonalAccessTokenType.USER_TOKEN.equals(personalAccessTokenEntity.getTokenType())) {
                personalAccessTokenInfoDto.setUser(personalAccessTokenEntity.getUserEntity().getLogin());
            }
            boolean valid = personalAccessTokenEntity.getExpireDate().isAfter(LocalDateTime.now());
            personalAccessTokenInfoDto.setValid(valid);
        }
        log.debug("Personal access token validity [{}]", personalAccessTokenInfoDto.isValid());
        return personalAccessTokenInfoDto;
    }

    private PersonalAccessTokenEntity saveToken(CreatePersonalAccessTokenDto createPersonalAccessTokenDto,
                                                UserEntity userEntity,
                                                String token) {
        PersonalAccessTokenEntity personalAccessTokenEntity = new PersonalAccessTokenEntity();
        personalAccessTokenEntity.setName(createPersonalAccessTokenDto.getName());
        personalAccessTokenEntity.setTokenType(createPersonalAccessTokenDto.getTokenType());
        personalAccessTokenEntity.setToken(md5Hex(token));
        personalAccessTokenEntity.setExpireDate(
                LocalDateTime.now().plusMonths(createPersonalAccessTokenDto.getExpirationMonth()));
        personalAccessTokenEntity.setUserEntity(userEntity);
        personalAccessTokenEntity.setCreated(LocalDateTime.now());
        return personalAccessTokenRepository.save(personalAccessTokenEntity);
    }

    private UserEntity getUser(String user) {
        return userEntityRepository.findByLogin(user).orElseThrow(
                () -> new EntityNotFoundException(UserEntity.class, user));
    }

    public static void main(String[] a) {
        StringKeyGenerator accessTokenGenerator =
                new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), TOKEN_LENGTH);
        String token = accessTokenGenerator.generateKey();
        System.out.println(token);
        System.out.println(md5Hex(token));
    }
}
