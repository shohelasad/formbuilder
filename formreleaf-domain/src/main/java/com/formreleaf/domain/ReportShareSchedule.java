package com.formreleaf.domain;

import com.formreleaf.domain.enums.ReportFormat;
import com.formreleaf.domain.enums.ReportSharingFrequency;
import com.formreleaf.validation.ValidateDateRange;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bazlur Rahman Rokon
 * @since 8/3/15.
 */
@Entity
@Table(name = "rpt_schedule")
@ValidateDateRange(start = "startDate", end = "endDate")
public class ReportShareSchedule extends BaseEntity<Long> implements NonDeletable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rpt_recipient_emails")
    private List<String> recipientEmails = new ArrayList<>();

    @NotNull(message = "Please select report share frequency")
    @Column(name = "frequency", length = 10)
    @Enumerated(EnumType.STRING)
    private ReportSharingFrequency reportSharingFrequency;

    @NotNull(message = "Please seelect a type of report")
    @Column(length = 5)
    @Enumerated(EnumType.STRING)
    private ReportFormat reportFormat;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private LocalDateTime startDate;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    private LocalDateTime endDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(nullable = true)
    private LocalDateTime lastRunDate;

    private Long runCount = 0l; //default value
    private Boolean deleted = false; //default value
    private Boolean expired = false; //default value

    @ManyToOne
    private Report report;

    @NotEmpty(message = "Please put at least one recipient's email address")
    @Transient
    private String emails;

    public ReportShareSchedule setId(Long id) {
        this.id = id;
        return this;
    }

    public List<String> getRecipientEmails() {
        return recipientEmails;
    }

    public ReportShareSchedule setRecipientEmails(List<String> recipientEmails) {
        this.recipientEmails = recipientEmails;
        return this;
    }

    public ReportSharingFrequency getReportSharingFrequency() {
        return reportSharingFrequency;
    }

    public ReportShareSchedule setReportSharingFrequency(ReportSharingFrequency reportSharingFrequency) {
        this.reportSharingFrequency = reportSharingFrequency;
        return this;
    }

    public ReportFormat getReportFormat() {
        return reportFormat;
    }

    public ReportShareSchedule setReportFormat(ReportFormat reportFormat) {
        this.reportFormat = reportFormat;
        return this;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public ReportShareSchedule setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public ReportShareSchedule setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
        return this;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public Boolean getExpired() {
        return expired;
    }

    public ReportShareSchedule setExpired(Boolean expired) {
        this.expired = expired;
        return this;
    }

    public LocalDateTime getLastRunDate() {
        return lastRunDate;
    }

    public ReportShareSchedule setLastRunDate(LocalDateTime lastRunDate) {
        this.lastRunDate = lastRunDate;
        return this;
    }

    public Long getRunCount() {
        return runCount;
    }

    public ReportShareSchedule setRunCount(Long runCount) {
        this.runCount = runCount;
        return this;
    }

    public ReportShareSchedule setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public Report getReport() {
        return report;
    }

    public ReportShareSchedule setReport(Report report) {
        this.report = report;
        return this;
    }

    public ReportShareSchedule setEmails(String emails) {
        this.emails = emails;
        return this;
    }

    public String getEmails() {
        return emails;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ReportShareSchedule{");
        sb.append("id=").append(id);
        sb.append(", recipientEmails=").append(recipientEmails);
        sb.append(", reportSharingFrequency=").append(reportSharingFrequency);
        sb.append(", reportFormat=").append(reportFormat);
        sb.append(", startDate=").append(startDate);
        sb.append(", endDate=").append(endDate);
        sb.append(", lastRunDate=").append(lastRunDate);
        sb.append(", runCount=").append(runCount);
        sb.append(", deleted=").append(deleted);
        sb.append(", report=").append(report);
        sb.append(", emails='").append(emails).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getRecipients() {

        return String.join(", ", recipientEmails);
    }
}
