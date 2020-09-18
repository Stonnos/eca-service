package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.CommonConfig;
import com.ecaservice.oauth.dto.CreateUserDto;
import com.ecaservice.oauth.entity.RoleEntity;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.exception.EntityNotFoundException;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.repository.RoleRepository;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.ecaservice.oauth.entity.UserEntity_.CREATION_DATE;
import static com.ecaservice.oauth.util.FilterUtils.buildSort;
import static com.ecaservice.oauth.util.FilterUtils.buildSpecification;
import static com.ecaservice.user.model.Role.ROLE_ECA_USER;

/**
 * Users management service.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final CommonConfig commonConfig;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserEntityRepository userEntityRepository;
    private final RoleRepository roleRepository;

    /**
     * Gets the next page for specified page request.
     *
     * @param pageRequestDto - page request
     * @return entities page
     */
    public Page<UserEntity> getNextPage(PageRequestDto pageRequestDto) {
        Sort sort = buildSort(pageRequestDto.getSortField(), CREATION_DATE, pageRequestDto.isAscending());
        Specification<UserEntity> specification = buildSpecification(pageRequestDto);
        int pageSize = Integer.min(pageRequestDto.getSize(), commonConfig.getMaxPageSize());
        return userEntityRepository.findAll(specification, PageRequest.of(pageRequestDto.getPage(), pageSize, sort));
    }

    /**
     * Creates user.
     *
     * @param createUserDto - create user dto
     * @param password      - user password
     * @return user entity
     */
    public UserEntity createUser(CreateUserDto createUserDto, String password) {
        UserEntity userEntity = userMapper.map(createUserDto);
        userEntity.setPassword(passwordEncoder.encode(password));
        populateUserRole(userEntity);
        userEntity.setCreationDate(LocalDateTime.now());
        return userEntityRepository.save(userEntity);
    }

    /**
     * Enable/Disable two factor authentication for user.
     *
     * @param userId     - user id
     * @param tfaEnabled - tfa enabled?
     */
    public void setTfaEnabled(long userId, boolean tfaEnabled) {
        UserEntity userEntity = userEntityRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(UserEntity.class, userId));
        userEntity.setTfaEnabled(tfaEnabled);
        userEntityRepository.save(userEntity);
    }

    private void populateUserRole(UserEntity userEntity) {
        RoleEntity roleEntity = roleRepository.findByRoleName(ROLE_ECA_USER).orElseThrow(
                () -> new IllegalStateException(String.format("Role with name [%s] doesn't exists", ROLE_ECA_USER)));
        userEntity.setRoles(Sets.newHashSet(roleEntity));
    }
}
