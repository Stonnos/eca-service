package com.ecaservice.oauth2.test.configuration;

import com.ecaservice.user.model.Role;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

/**
 * Spring security oauth2 configuration.
 *
 * @author Roman Batygin
 */
@TestConfiguration
@EnableWebSecurity
@RequiredArgsConstructor
public class Oauth2ServerSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final Oauth2TestConfig oauth2TestConfig;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(oauth2TestConfig.getUsername())
                .password(passwordEncoder().encode(oauth2TestConfig.getPassword()))
                .authorities(Collections.singletonList(new Role(Role.ROLE_SUPER_ADMIN, StringUtils.EMPTY)));
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

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated().and().csrf().disable();
    }
}