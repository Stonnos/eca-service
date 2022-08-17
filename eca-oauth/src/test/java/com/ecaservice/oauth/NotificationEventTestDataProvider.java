package com.ecaservice.oauth;

import com.ecaservice.oauth.event.model.ChangeEmailRequestNotificationEvent;
import com.ecaservice.oauth.event.model.ChangePasswordRequestNotificationEvent;
import com.ecaservice.oauth.event.model.EmailChangedNotificationEvent;
import com.ecaservice.oauth.event.model.PasswordChangedNotificationEvent;
import com.ecaservice.oauth.event.model.PasswordResetNotificationEvent;
import com.ecaservice.oauth.event.model.ResetPasswordRequestNotificationEvent;
import com.ecaservice.oauth.event.model.TfaCodeNotificationEvent;
import com.ecaservice.oauth.event.model.UserCreatedEvent;
import com.ecaservice.oauth.event.model.UserLockedNotificationEvent;
import com.ecaservice.oauth.event.model.UserUnLockedNotificationEvent;
import com.ecaservice.oauth.model.TokenModel;
import com.ecaservice.oauth.service.mail.dictionary.Templates;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import static com.ecaservice.oauth.TestHelperUtils.createUserEntity;

public class NotificationEventTestDataProvider implements ArgumentsProvider {

    private static final String TFA_CODE = "code";
    private static final String PASSWORD = "pa66word!";
    private static final String TOKEN = "token";
    private static final String NEW_EMAIL = "newemail@mail.ru";
    private static final long TOKEN_ID = 1L;

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                tfaCodeEventArgs(),
                userCreatedEventArgs(),
                resetPasswordEventArgs(),
                changePasswordEventArgs(),
                changeEmailEventArgs(),
                emailChangedEventArgs(),
                passwordChangedEventArgs(),
                passwordResetEventArgs(),
                userLockedEventArgs(),
                userUnlockedEventArgs()
        );
    }

    private Arguments tfaCodeEventArgs() {
        var event = new TfaCodeNotificationEvent(this, createUserEntity(), TFA_CODE);
        return Arguments.of(event, Templates.TFA_CODE, event.getUserEntity().getEmail());
    }

    private Arguments userCreatedEventArgs() {
        var event = new UserCreatedEvent(this, createUserEntity(), PASSWORD);
        return Arguments.of(event, Templates.NEW_USER, event.getUserEntity().getEmail());
    }

    private Arguments resetPasswordEventArgs() {
        var userEntity = createUserEntity();
        var tokenModel = TokenModel.builder()
                .token(TOKEN)
                .tokenId(TOKEN_ID)
                .login(userEntity.getLogin())
                .email(userEntity.getEmail())
                .build();
        var event = new ResetPasswordRequestNotificationEvent(this, tokenModel);
        return Arguments.of(event, Templates.RESET_PASSWORD, userEntity.getEmail());
    }

    private Arguments changePasswordEventArgs() {
        var userEntity = createUserEntity();
        var tokenModel = TokenModel.builder()
                .token(TOKEN)
                .tokenId(TOKEN_ID)
                .login(userEntity.getLogin())
                .email(userEntity.getEmail())
                .build();
        var event = new ChangePasswordRequestNotificationEvent(this, tokenModel);
        return Arguments.of(event, Templates.CHANGE_PASSWORD, userEntity.getEmail());
    }

    private Arguments changeEmailEventArgs() {
        var userEntity = createUserEntity();
        var tokenModel = TokenModel.builder()
                .token(TOKEN)
                .tokenId(TOKEN_ID)
                .login(userEntity.getLogin())
                .email(userEntity.getEmail())
                .build();
        var event = new ChangeEmailRequestNotificationEvent(this, tokenModel, NEW_EMAIL);
        return Arguments.of(event, Templates.CHANGE_EMAIL, userEntity.getEmail());
    }

    private Arguments emailChangedEventArgs() {
        var event = new EmailChangedNotificationEvent(this, createUserEntity());
        return Arguments.of(event, Templates.EMAIL_CHANGED, event.getUserEntity().getEmail());
    }

    private Arguments passwordChangedEventArgs() {
        var event = new PasswordChangedNotificationEvent(this, createUserEntity());
        return Arguments.of(event, Templates.PASSWORD_CHANGED, event.getUserEntity().getEmail());
    }

    private Arguments passwordResetEventArgs() {
        var event = new PasswordResetNotificationEvent(this, createUserEntity());
        return Arguments.of(event, Templates.PASSWORD_RESET, event.getUserEntity().getEmail());
    }

    private Arguments userLockedEventArgs() {
        var event = new UserLockedNotificationEvent(this, createUserEntity());
        return Arguments.of(event, Templates.USER_LOCKED, event.getUserEntity().getEmail());
    }

    private Arguments userUnlockedEventArgs() {
        var event = new UserUnLockedNotificationEvent(this, createUserEntity());
        return Arguments.of(event, Templates.USER_UNLOCKED, event.getUserEntity().getEmail());
    }
}
