package com.ecaservice.oauth.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.oauth.config.ChangePasswordConfig;
import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.ChangePasswordRequestAlreadyExistsException;
import com.ecaservice.oauth.exception.InvalidTokenException;
import com.ecaservice.oauth.repository.ChangePasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.ecaservice.oauth.util.Utils.generateToken;

/**
 * Change password service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePasswordService {

    private final ChangePasswordConfig changePasswordConfig;
    private final PasswordEncoder passwordEncoder;
    private final ChangePasswordRequestRepository changePasswordRequestRepository;
    private final UserEntityRepository userEntityRepository;

    /**
     * Create change password request.
     *
     * @param userId                - user id
     * @param changePasswordRequest - change password request
     * @return change password request entity
     */
    public ChangePasswordRequestEntity createChangePasswordRequest(Long userId,
                                                                   ChangePasswordRequest changePasswordRequest) {
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class, userId));
        LocalDateTime now = LocalDateTime.now();
        ChangePasswordRequestEntity changePasswordRequestEntity =
                changePasswordRequestRepository.findByUserEntityAndExpireDateAfterAndApproveDateIsNull(userEntity, now);
        if (changePasswordRequestEntity != null) {
            throw new ChangePasswordRequestAlreadyExistsException(userId);
        }
        changePasswordRequestEntity = new ChangePasswordRequestEntity();
        changePasswordRequestEntity.setToken(generateToken(userEntity));
        changePasswordRequestEntity.setExpireDate(now.plusMinutes(changePasswordConfig.getValidityMinutes()));
        String encodedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword().trim());
        changePasswordRequestEntity.setNewPassword(encodedPassword);
        changePasswordRequestEntity.setUserEntity(userEntity);
        return changePasswordRequestRepository.save(changePasswordRequestEntity);
    }

    /**
     * Change user password.
     *
     * @param token - token value
     */
    @Transactional
    public void changePassword(String token) {
        ChangePasswordRequestEntity changePasswordRequestEntity =
                changePasswordRequestRepository.findByTokenAndExpireDateAfterAndApproveDateIsNull(token,
                        LocalDateTime.now()).orElseThrow(() -> new InvalidTokenException(token));
        UserEntity userEntity = changePasswordRequestEntity.getUserEntity();
        userEntity.setPassword(changePasswordRequestEntity.getNewPassword());
        changePasswordRequestEntity.setApproveDate(LocalDateTime.now());
        userEntityRepository.save(userEntity);
        changePasswordRequestRepository.save(changePasswordRequestEntity);
        log.info("New password has been set for user [{}], change password request id [{}]", userEntity.getLogin(),
                changePasswordRequestEntity.getId());
    }
}
