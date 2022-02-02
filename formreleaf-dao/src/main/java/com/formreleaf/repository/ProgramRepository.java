package com.formreleaf.repository;

import com.formreleaf.domain.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface ProgramRepository extends JpaRepository<Program, Long>, NonDeletableRepository<Program, Long> {

    List<Program> findByName(String name);
    
    @Query("select p.name from Program p where p.name like :name%")
    List<Program> findBySimilarName(@Param("name") String name);

    Optional<Program> findOneById(Long id);

    @Query("SELECT o FROM Program o WHERE o.organization.id =:organizationId AND o.deleted = false ORDER BY o.name ASC")
    Page<Program> findProgramByOrganizationId(Pageable pageable, @Param("organizationId") Long organizationId);

    @Query("SELECT o FROM Program o WHERE o.organization.id =:organizationId and o.publish.publishStatus = 'PUBLIC' and o.deleted = false")
    Set<Program> findPublishedProgramByOrganizationId(@Param("organizationId") Long organizationId);

    @Query("select p from Program p where p.deleted = false")
    Page<Program> findAll(Pageable pageable);

    @Query("select p.formTemplate from Program p where p.deleted = false and p.id=:id")
    String findFormTemplateByProgramId(@Param(value = "id") Long id);

    Optional<Program> findBySlug(String slug);

    @Query("SELECT o.id FROM Program o WHERE o.organization.id =:organizationId and o.deleted = false")
    Set<Long> findProgramIdsByOrganizationId(@Param("organizationId") Long organizationId);

    Optional<Program> findByOrganization_SlugAndSlug(String oslug, String pslug);
}
