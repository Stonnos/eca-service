package com.ecaservice.oauth.service;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.UserEntityRepository;
import com.ecaservice.user.model.Role;
import com.ecaservice.user.model.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implements user details service.
 *
 * @author Roman Batygin
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param userEntityRepository - user entity repository bean
     */
    @Inject
    public UserDetailsServiceImpl(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userEntityRepository.findByLogin(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException(String.format("User with login %s doesn't exists!", username));
        }
        List<Role> authorities =
                userEntity.getRoles().stream().map(roleEntity -> new Role(roleEntity.getRoleName())).collect(
                        Collectors.toList());
        return new UserDetailsImpl(userEntity.getLogin(), userEntity.getPassword(), userEntity.getEmail(),
                userEntity.getFirstName(), authorities);
    }
}
