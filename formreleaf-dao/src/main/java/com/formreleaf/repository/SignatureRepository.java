package com.formreleaf.repository;

import com.formreleaf.domain.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Bazlur Rahman Rokon
 * @date 6/3/15.
 */
@Repository
public interface SignatureRepository extends JpaRepository<Signature, Long> {
    Optional<Signature> findOneById(Long id);
}
