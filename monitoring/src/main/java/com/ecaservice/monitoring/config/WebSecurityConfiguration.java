package com.ecaservice.monitoring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * Security configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String TARGET_URL_PARAMETER = "redirectTo";
    private static final String DEFAULT_TARGET_URL = "/";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter(TARGET_URL_PARAMETER);
        successHandler.setDefaultTargetUrl(DEFAULT_TARGET_URL);
        http.authorizeRequests()
                .antMatchers("/assets/**").permitAll()
                .antMatchers("/login.html").permitAll()
                .anyRequest().authenticated().and()
                .formLogin().loginPage("/login.html")
                .successHandler(successHandler).and()
                .logout().logoutUrl("/logout").and()
                .httpBasic().and()
                .csrf().disable();
    }
}
