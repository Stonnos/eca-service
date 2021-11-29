package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.model.entity.ErsRequestCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository to manage with {@link ErsRequestCache} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ErsRequestCacheRepository extends JpaRepository<ErsRequestCache, Long> {

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
    @Query("select ec.id from ErsRequestCache ec where ec.ersRequest = :ersRequest")
    Optional<Long> findRequestCacheId(@Param("ersRequest") ErsRequest ersRequest);
}
