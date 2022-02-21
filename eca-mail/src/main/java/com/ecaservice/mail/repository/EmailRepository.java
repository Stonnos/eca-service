package com.ecaservice.mail.repository;

import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.model.EmailStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link Email} persistence entity.
 *
 * @author Roman Batygin
 */
public interface EmailRepository extends JpaRepository<Email, Long> {

    /**
     * Check email existing with specified uuid.
     *
     * @param uuid - request uuid
     * @return {@code true} if email with specified request id exists, otherwise {@link false}
     */
   boolean existsByUuid(String uuid);

    /**
     * Finds not sent emails by statuses.
     *
     * @param statuses - {@link EmailStatus} collection
     * @param pageable - {@link Pageable} object
     * @return emails list
     */
    List<Email> findByStatusNotIn(Collection<EmailStatus> statuses, Pageable pageable);
}
