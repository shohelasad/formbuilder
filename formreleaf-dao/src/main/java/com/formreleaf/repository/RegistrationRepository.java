package com.formreleaf.repository;


import com.formreleaf.domain.Program;
import com.formreleaf.domain.Registration;
import com.formreleaf.domain.enums.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @uathor Bazlur Rahman Rokon
 * @since 4/30/15.
 */
@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Optional<Registration> findOneById(Long id);

    @Query("SELECT o FROM Registration o where o.registrant.id=:userId and o.deleted=false")
    Set<Registration> findAllRegistrationsByUserId(@Param(value = "userId") Long userId);

    @Query("select u from Registration u where u.deleted=false")
    Page<Registration> findAll(Pageable pageable);

    Page<Registration> findAllByProgram_IdAndRegistrationStatusAndDeletedFalse(Long id, RegistrationStatus registrationStatus, Pageable pageable);

    List<Registration> findAllByProgram_IdAndRegistrationStatusAndDeletedFalseOrderByLastNameAsc(Long id, RegistrationStatus registrationStatus);

    List<Registration> findAllByDeletedFalseAndProgram_IdOrderByRegistrantNameAsc(Long id);

    long countByProgramAndRegistrationStatus(Program program, RegistrationStatus registrationStatus);

    Set<Registration> findAllByRegistrantNameContainingAndDeletedFalseAndProgram_IdIn(String registrantName, List<Long> ids);

    List<Registration> findAllByProgram_IdAndRegistrant_IdOrderByLastModifiedDateDesc(Long programId, Long registrantId);
}
