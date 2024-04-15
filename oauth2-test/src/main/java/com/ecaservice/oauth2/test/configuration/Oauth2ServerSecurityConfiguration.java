package com.ecaservice.oauth2.test.configuration;

import com.ecaservice.user.model.Role;
import com.ecaservice.user.model.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

/**
 * Spring security oauth2 configuration.
 *
 * @author Roman Batygin
 */
@TestConfiguration
@EnableWebSecurity
@RequiredArgsConstructor
public class Oauth2ServerSecurityConfiguration {

    private static final long USER_ID = 1L;

    private final Oauth2TestConfig oauth2TestConfig;

    @Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> {
            var userDetails = new UserDetailsImpl();
            userDetails.setId(USER_ID);
            userDetails.setUserName(oauth2TestConfig.getUsername());
            userDetails.setPassword(passwordEncoder().encode(oauth2TestConfig.getPassword()));
            userDetails.setAuthorities(Collections.singletonList(new Role(Role.ROLE_SUPER_ADMIN, StringUtils.EMPTY)));
            return userDetails;
        }).passwordEncoder(passwordEncoder());
    }

    /**
     * Creates password encoder bean.
     *
     * @return password encoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .csrf()
                .disable();
        return http.build();
    }
}
