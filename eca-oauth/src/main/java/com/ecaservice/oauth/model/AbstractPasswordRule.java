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
        @JsonSubTypes.Type(value = MinLengthRule.class, name = "MIN_LENGTH"),
        @JsonSubTypes.Type(value = SpecialCharacterRule.class, name = "SPECIAL_CHARACTER"),
        @JsonSubTypes.Type(value = LowerCaseCharacterRule.class, name = "LOWER_CASE_CHARACTER"),
        @JsonSubTypes.Type(value = UpperCaseCharacterRule.class, name = "UPPER_CASE_CHARACTER"),
        @JsonSubTypes.Type(value = DigitRule.class, name = "DIGIT"),
        @JsonSubTypes.Type(value = RepeatCharacterRule.class, name = "REPEAT_CHARACTERS"),
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
