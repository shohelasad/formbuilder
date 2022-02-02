package com.formreleaf.repository;

import com.formreleaf.domain.Agreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @author Bazlur Rahman Rokon
 * @date 4/27/15.
 */
@Repository
@Transactional
public interface AgreementRepository extends JpaRepository<Agreement, Long>, NonDeletableRepository<Agreement, Long> {

    Set<Agreement> findAllAgreementsByOrganization_IdAndDeletedFalse(Long id);

}
