package com.ecaservice.oauth.service;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.mapping.RoleMapperImpl;
import com.ecaservice.oauth.mapping.UserMapperImpl;
import com.ecaservice.oauth.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link UserDetailsServiceImpl} functionality.
 *
 * @author Roman Batygin
 */
@Import({UserDetailsServiceImpl.class, UserMapperImpl.class, RoleMapperImpl.class})
class UserDetailsServiceImplTest extends AbstractJpaTest {

    private static final String USER = "user";

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public void deleteAll() {
        userEntityRepository.deleteAll();
    }

    @Test
    void testUserNameNotFound() {
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(USER));
    }

    @Test
    void testLoadUserDetailsByLogin() {
        UserEntity userEntity = createAndSaveUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEntity.getLogin());
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(userEntity.getLogin());
    }

    @Test
    void testLoadUserDetailsByEmail() {
        UserEntity userEntity = createAndSaveUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEntity.getEmail());
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(userEntity.getLogin());
    }

    private UserEntity createAndSaveUser() {
        UserEntity userEntity = createUserEntity();
        userEntity.setRoles(Collections.emptySet());
        return userEntityRepository.save(userEntity);
    }
}
