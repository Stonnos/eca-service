package com.ecaservice.oauth.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Abstract password rule.
 *
 * @author Roman Batygin
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "ruleType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MinLengthRuleModel.class, name = "MIN_LENGTH"),
        @JsonSubTypes.Type(value = CharacterRuleModel.class, name = "CHARACTER"),
        @JsonSubTypes.Type(value = RepeatCharactersRuleModel.class, name = "REPEAT_CHARACTERS"),
})
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractPasswordRule {

    /**
     * Rule type
     */
    @Getter
    private final PasswordRuleType ruleType;

    /**
     * Message type
     */
    @Setter
    @Getter
    private String message;

    /**
     * Applies visitor.
     *
     * @param <T>                 result generic type
     * @param passwordRuleVisitor - visitor interface
     */
    public abstract <T> T handle(PasswordRuleVisitor<T> passwordRuleVisitor);
}
