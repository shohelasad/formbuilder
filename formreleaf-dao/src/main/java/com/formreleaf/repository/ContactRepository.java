package com.formreleaf.repository;

import com.formreleaf.domain.Contact;
import com.formreleaf.domain.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * @author Bazlur Rahman Rokon
 * @date 4/26/15.
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Set<Contact> findByProgram(Program program);

    Optional<Contact> findOneById(Long id);
}
