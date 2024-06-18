package com.ecaservice.oauth.service;

import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Implements user details service.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity userEntity = userEntityRepository.findUser(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException(
                    String.format("User with login or email [%s] doesn't exists!", username));
        }
        var authorities = userEntity.getRoles().stream()
                .map(roleEntity -> new SimpleGrantedAuthority(roleEntity.getRoleName()))
                .collect(Collectors.toList());
        return new User(userEntity.getLogin(),
                userEntity.getPassword(),
                true,
                true,
                true,
                !userEntity.isLocked(),
                authorities
        );
    }
}
