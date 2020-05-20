package com.ecaservice.oauth.service;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.mapping.UserMapper;
import com.ecaservice.oauth.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implements user details service.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;
    private final UserEntityRepository userEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity userEntity = userEntityRepository.findByLogin(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException(String.format("User with login %s doesn't exists!", username));
        }
        return userMapper.mapDetails(userEntity);
    }
}
