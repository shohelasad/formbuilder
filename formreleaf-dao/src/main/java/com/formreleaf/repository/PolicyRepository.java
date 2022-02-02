package com.formreleaf.repository;

import com.formreleaf.domain.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @author Bazlur Rahman Rokon
 * @date 4/28/15.
 */
@Repository
@Transactional
public interface PolicyRepository extends JpaRepository<Policy, Long>, NonDeletableRepository<Policy, Long> {

    Set<Policy> findAllPolicyByOrganization_IdAndDeletedFalse(Long id);
}
