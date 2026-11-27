package com.ecaservice.oauth.service;

import com.ecaservice.oauth.mapping.MenuItemMapperImpl;
import com.ecaservice.web.dto.model.MenuItemDto;
import com.ecaservice.web.dto.model.UiPermissionsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UiPermissionsService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({MenuItemMapperImpl.class, UiPermissionsService.class})
class UiPermissionsServiceTest {

    private static final String USERNAME = "test-user";
    private static final String PASSWORD = "password";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    private static final String EXPERIMENTS_LABEL = "Эксперименты";
    private static final String CLASSIFIERS_LABEL = "Классификаторы";
    private static final String HISTORY_LABEL = "История результатов классификации";
    private static final String DATASETS_LABEL = "Датасеты";
    private static final String USERS_LABEL = "Пользователи";
    private static final String EMAIL_TEMPLATES_LABEL = "Шаблоны email сообщений";
    private static final String AUDIT_LOGS_LABEL = "Журнал аудита";
    private static final String SETTINGS_LABEL = "Настройки";
    private static final String CLASSIFIERS_CONFIG_LABEL = "Конфигурации классификаторов";

    @MockBean
    private SecurityContextProvider securityContextProvider;


    @Autowired
    private UiPermissionsService uiPermissionsService;

    @Test
    void testGetUiPermissionsForRegularUser() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                USERNAME,
                PASSWORD,
                List.of(new SimpleGrantedAuthority(ROLE_USER))
        );
        when(securityContextProvider.getAuthentication()).thenReturn(authentication);

        UiPermissionsDto result = uiPermissionsService.geUIPermissions();

        assertThat(result).isNotNull();
        assertThat(result.getMenuItems())
                .extracting(MenuItemDto::getLabel)
                .containsExactly(
                        EXPERIMENTS_LABEL,
                        CLASSIFIERS_LABEL,
                        HISTORY_LABEL,
                        DATASETS_LABEL,
                        SETTINGS_LABEL
                );
    }

    @Test
    void testGetUiPermissionsForSuperAdmin() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                USERNAME,
                PASSWORD,
                List.of(new SimpleGrantedAuthority(ROLE_SUPER_ADMIN))
        );
        when(securityContextProvider.getAuthentication()).thenReturn(authentication);

        UiPermissionsDto result = uiPermissionsService.geUIPermissions();

        assertThat(result).isNotNull();
        assertThat(result.getMenuItems())
                .extracting(MenuItemDto::getLabel)
                .containsExactly(
                        EXPERIMENTS_LABEL,
                        CLASSIFIERS_LABEL,
                        HISTORY_LABEL,
                        DATASETS_LABEL,
                        USERS_LABEL,
                        EMAIL_TEMPLATES_LABEL,
                        AUDIT_LOGS_LABEL,
                        SETTINGS_LABEL
                );

        MenuItemDto settingsItem = result.getMenuItems().get(7);
        assertThat(settingsItem.getItems()).hasSize(1);
        assertThat(settingsItem.getItems().getFirst().getLabel()).isEqualTo(EXPERIMENTS_LABEL);
        assertThat(settingsItem.getItems().getFirst().getItems())
                .extracting(MenuItemDto::getLabel)
                .containsExactly(CLASSIFIERS_CONFIG_LABEL);
    }
}
