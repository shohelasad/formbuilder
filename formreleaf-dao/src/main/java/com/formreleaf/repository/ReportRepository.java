package com.formreleaf.repository;

import com.formreleaf.domain.Organization;
import com.formreleaf.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByOrganizationAndDeletedFalseOrderByNameAsc(Organization organization);
    
}
