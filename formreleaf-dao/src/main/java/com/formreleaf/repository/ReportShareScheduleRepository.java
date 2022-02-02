package com.formreleaf.repository;

import com.formreleaf.domain.ReportShareSchedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Bazlur Rahman Rokon
 * @since 8/3/15.
 */
@Repository
public interface ReportShareScheduleRepository extends JpaRepository<ReportShareSchedule, Long> {

    List<ReportShareSchedule> findAllByDeletedFalseAndExpiredFalse();
    
    @Modifying(clearAutomatically=true)
	@Query("update ReportShareSchedule set runCount = :runCount, lastRunDate = now() where id = :id")
    void update(@Param("id") Long id, @Param("runCount") Long runCount);
}
