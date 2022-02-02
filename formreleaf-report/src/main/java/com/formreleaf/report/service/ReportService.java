package com.formreleaf.report.service;

import com.formreleaf.common.dto.BlockDto;
import com.formreleaf.common.dto.ProgramDto;
import com.formreleaf.common.dto.ReportDefinitionDto;
import com.formreleaf.domain.Organization;
import com.formreleaf.domain.Report;
import com.formreleaf.domain.ReportShareSchedule;
import com.formreleaf.domain.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * @author Bazlur Rahman Rokon
 * @since 9/28/15.
 */
public interface ReportService {
    Report save(ReportDefinitionDto report, Organization organization);

    Report findById(Long id);

    List<Report> findReportsByOrganization(Organization organization);

    void delete(Long id);

    ResponseEntity<?> downloadCsv(Long reportId);

    ResponseEntity<?> downloadPdf(Long reportId);

    ReportDefinitionDto getReportDefinition(Long reportId, User currentLoggedInUser);

    List<ProgramDto> findAllSelectableProgram(User currentLoggedUser);

    List<BlockDto> findAllSelectableBlocks(Organization organization);

    Report findOne(Long reportId);

    ReportShareSchedule saveReportSchedule(ReportShareSchedule reportShareSchedule);

    List<ReportShareSchedule> findAllReportShareSchedule(Long reportId);

    ReportShareSchedule findReportShareSchedule(Long reportShareScheduleId);

    ReportShareSchedule deleteReportShareSchedule(Long reportShareScheduleId);

    List<Report> findAllReports();

    void updateExpiredScheduler();

    byte[] getCsvContent(Report report);

    byte[] getPdfContent(Report report);

    void updateReportScheduler(ReportShareSchedule reportShareSchedule);

    byte[] getContentsAsByte(Long reportShareScheduleId);

}
