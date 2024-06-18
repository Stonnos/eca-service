package com.ecaservice.oauth2.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * Oauth2 utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Oauth2Utils {

    /**
     * Populates authorities from claims property.
     *
     * @param claims                   - claims
     * @param attributeName            - claims attribute name
     * @param authorityMappingFunction - authority mapping function
     * @param authorities              - authorities list
     */
    public static void populateAuthorities(Map<String, Object> claims,
                                           String attributeName,
                                           Function<String, String> authorityMappingFunction,
                                           Collection<GrantedAuthority> authorities) {
        Object authoritiesValue = claims.get(attributeName);
        if (authoritiesValue != null) {
            Collection<String> authoritiesList = (Collection<String>) authoritiesValue;
            for (String authorityValue : authoritiesList) {
                authorities.add(new SimpleGrantedAuthority(authorityMappingFunction.apply(authorityValue)));
            }
        }
    }
}
