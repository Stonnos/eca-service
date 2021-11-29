package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.model.entity.ErsRetryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository to manage with {@link ErsRetryRequest} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ErsRetryRequestRepository extends JpaRepository<ErsRetryRequest, Long> {

    /**
     * Checks ers request cache.
     *
     * @param ersRequest - ers request
     * @return {@code true} if ers request cache exists, {@code false} otherwise
     */
    boolean existsByErsRequest(ErsRequest ersRequest);

    /**
     * Gets ers request cache id.
     *
     * @param ersRequest - ers request
     * @return ers request cache id
     */
    @Query("select ec.id from ErsRetryRequest ec where ec.ersRequest = :ersRequest")
    Optional<Long> findRequestCacheId(@Param("ersRequest") ErsRequest ersRequest);

    /**
     * Gets not sent ers requests ids.
     *
     * @return ids list
     */
    @Query("select er.id from ErsRetryRequest er")
    List<Long> getNotSentIds();

    /**
     * Gets ers retry requests page.
     *
     * @param ids      - ids list
     * @param pageable - pageable function
     * @return ers retry requests page
     */
    Page<ErsRetryRequest> findByIdIn(Collection<Long> ids, Pageable pageable);
}
