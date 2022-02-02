package com.formreleaf.repository;

import com.formreleaf.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Bazlur Rahman Rokon
 * @since 9/3/15.
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Document findByUuid(String uuid);
}
