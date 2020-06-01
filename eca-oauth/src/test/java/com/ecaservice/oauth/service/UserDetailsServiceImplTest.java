package com.ecaservice.oauth.service;

import com.ecaservice.oauth.AbstractJpaTest;
import com.ecaservice.oauth.entity.UserEntity;
import com.ecaservice.oauth.mapping.RoleMapperImpl;
import com.ecaservice.oauth.mapping.UserMapperImpl;
import com.ecaservice.oauth.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.inject.Inject;
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
public class UserDetailsServiceImplTest extends AbstractJpaTest {

    private static final String USER = "user";

    @Inject
    private UserEntityRepository userEntityRepository;

    @Inject
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
    void testLoadUserDetails() {
        UserEntity userEntity = createUserEntity();
        userEntity.setRoles(Collections.emptySet());
        userEntityRepository.save(userEntity);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEntity.getLogin());
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(userEntity.getLogin());
    }
}
