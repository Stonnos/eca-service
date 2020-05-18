package com.notification.repository;

import com.notification.model.Email;
import com.notification.model.EmailStatus;
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
     * Finds not sent emails by statuses.
     *
     * @param statuses - {@link EmailStatus} collection
     * @param pageable - {@link Pageable} object
     * @return emails list
     */
    List<Email> findByStatusNotInOrderBySaveDate(Collection<EmailStatus> statuses, Pageable pageable);
}
