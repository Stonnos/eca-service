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
     * Gets active change password request or save new if not exists.
     *
     * @param changePasswordRequest - change password request
     * @return change password request entity
     */
    public ChangePasswordRequestEntity getOrSaveChangePasswordRequest(ChangePasswordRequest changePasswordRequest) {
        UserEntity userEntity = userEntityRepository.findById(changePasswordRequest.getUserId())
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Can't create change password request, because user with id %d doesn't exists!",
                                changePasswordRequest.getUserId())));
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
}
