package com.formreleaf.repository;

import com.formreleaf.domain.DocumentDownloadHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Bazlur Rahman Rokon
 * @since 9/14/15.
 */
@Repository
public interface DocumentDownloadHistoryRepository extends JpaRepository<DocumentDownloadHistory, Long> {
}
