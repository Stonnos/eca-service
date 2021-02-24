package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.ChangePasswordConfig;
import com.ecaservice.oauth.dto.ChangePasswordRequest;
import com.ecaservice.oauth.entity.ChangePasswordRequestEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.ChangePasswordRequestRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.function.Supplier;

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
     * Gets active change password request or save new if not exists.
     *
     * @param userId - user id
     * @param changePasswordRequest - change password request
     * @return change password request entity
     */
    public ChangePasswordRequestEntity getOrSaveChangePasswordRequest(Long userId,
                                                                      ChangePasswordRequest changePasswordRequest) {
        UserEntity userEntity = userEntityRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't create change password request, because user with id %d doesn't exists!",
                                userId)));
        LocalDateTime now = LocalDateTime.now();
        ChangePasswordRequestEntity changePasswordRequestEntity =
                changePasswordRequestRepository.findByUserEntityAndExpireDateAfterAndApproveDateIsNull(userEntity, now);
        if (changePasswordRequestEntity == null) {
            changePasswordRequestEntity = new ChangePasswordRequestEntity();
            changePasswordRequestEntity.setToken(generateToken(userEntity));
            changePasswordRequestEntity.setExpireDate(now.plusMinutes(changePasswordConfig.getValidityMinutes()));
            String encodedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword().trim());
            changePasswordRequestEntity.setNewPassword(encodedPassword);
            changePasswordRequestEntity.setUserEntity(userEntity);
            changePasswordRequestRepository.save(changePasswordRequestEntity);
        }
        return changePasswordRequestEntity;
    }

    /**
     * Change user password.
     *
     * @param token - token value
     */
    @Transactional
    public void changePassword(String token) {
        Supplier<IllegalStateException> invalidTokenError =
                () -> new IllegalStateException(String.format("Invalid token [%s]", token));
        ChangePasswordRequestEntity changePasswordRequestEntity =
                changePasswordRequestRepository.findByTokenAndExpireDateAfterAndApproveDateIsNull(token,
                        LocalDateTime.now()).orElseThrow(invalidTokenError);
        UserEntity userEntity = changePasswordRequestEntity.getUserEntity();
        userEntity.setPassword(changePasswordRequestEntity.getNewPassword());
        changePasswordRequestEntity.setApproveDate(LocalDateTime.now());
        userEntityRepository.save(userEntity);
        changePasswordRequestRepository.save(changePasswordRequestEntity);
        log.info("New password has been set for user [{}], change password request id [{}]", userEntity.getLogin(),
                changePasswordRequestEntity.getId());
    }
}
