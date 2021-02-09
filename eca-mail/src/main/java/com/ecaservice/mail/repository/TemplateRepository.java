package com.ecaservice.mail.repository;

import com.ecaservice.mail.model.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository to manage with {@link TemplateEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {

    /**
     * Gets template body by code.
     *
     * @param code - template code
     * @return template body
     */
    @Query("select t.body from TemplateEntity t where t.code = :code")
    String getBodyByCode(@Param("code") String code);

    /**
     * Finds template by code.
     *
     * @param code - template code
     * @return template entity
     */
    Optional<TemplateEntity> findByCode(String code);
}
