package com.ecaservice.auto.test.model;

import eca.core.DescriptiveEnum;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * Email type enum.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum EmailType implements DescriptiveEnum {

    /**
     * New experiment email
     */
    NEW("Создан новый эксперимент") {
        @Override
        public void handle(EmailTypeVisitor visitor) {
            visitor.visitNewExperiment();
        }
    },

    /**
     * In progress experiment email
     */
    IN_PROGRESS("Заявка поступила в работу") {
        @Override
        public void handle(EmailTypeVisitor visitor) {
            visitor.visitInProgressExperiment();
        }
    },

    /**
     * Finished experiment email
     */
    FINISHED("Эксперимент завершен") {
        @Override
        public void handle(EmailTypeVisitor visitor) {
            visitor.visitFinishedExperiment();
        }
    },

    /**
     * Error experiment email
     */
    ERROR("Ошибка при построении эксперимента") {
        @Override
        public void handle(EmailTypeVisitor visitor) {
            visitor.visitErrorExperiment();
        }
    },

    /**
     * Timeout experiment email
     */
    TIMEOUT("Таймаут при построении эксперимента"){
        @Override
        public void handle(EmailTypeVisitor visitor) {
            visitor.visitTimeoutExperiment();
        }
    };

    /**
     * Email subject
     */
    private final String description;

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Visitor method.
     *
     * @param visitor - visitor interface
     */
    public abstract void handle(EmailTypeVisitor visitor);

    /**
     * Finds email type by description.
     *
     * @param description - description (email subject)
     * @return email type enum value
     */
    public static EmailType findByDescription(String description) {
        return Stream.of(values())
                .filter(emailType -> emailType.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Can't find email type with description [%s]", description)));
    }
}
