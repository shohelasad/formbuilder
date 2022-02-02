package com.formreleaf.repository;

import com.formreleaf.domain.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    String FIND_BY_USER_QUERY = "SELECT o " +
            "FROM Organization o LEFT JOIN o.users u " +
            "WHERE u.id = :id";

    Optional<Organization> findByName(String name);

    Optional<Organization> findBySlug(String slug);

    Optional<Organization> findOneById(Long id);

    Page<Organization> findAllByDeletedFalseOrderByNameAsc(Pageable pageable);

    @Query(FIND_BY_USER_QUERY)
    List<Organization> findOrganizationByUser(@Param("id") Long id);
}
