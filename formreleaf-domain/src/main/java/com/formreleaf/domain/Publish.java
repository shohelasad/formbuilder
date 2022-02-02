package com.formreleaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.formreleaf.domain.enums.ProgramStatus;
import com.formreleaf.domain.enums.PublishStatus;
import com.formreleaf.validation.ValidateDateRange;
import com.formreleaf.validation.ValidateDateRangeList;

import javax.persistence.*;
import java.util.Date;


@Entity
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@ValidateDateRangeList(value = {
        @ValidateDateRange(start = "registrationOpenDate", end = "registrationCloseDate"),
        @ValidateDateRange(start = "publishStartDate", end = "publishEndDate")
})
public class Publish {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private ProgramStatus programStatus = ProgramStatus.DRAFT;

    @Column(nullable = true)
    private Date registrationOpenDate;

    @Column(nullable = true)
    private Date registrationCloseDate;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private PublishStatus publishStatus = PublishStatus.PRIVATE;

    @Column(nullable = true)
    private Date publishStartDate;

    @Column(nullable = true)
    private Date publishEndDate;
    
    private boolean registrationOpened = false;
    
    private boolean programOpened = false;

    public ProgramStatus getProgramStatus() {
        return programStatus;
    }

    public void setProgramStatus(ProgramStatus programStatus) {
        this.programStatus = programStatus;
    }

    public Date getRegistrationOpenDate() {
        return registrationOpenDate;
    }

    public void setRegistrationOpenDate(Date registrationOpenDate) {
        this.registrationOpenDate = registrationOpenDate;
    }

    public Date getRegistrationCloseDate() {
        return registrationCloseDate;
    }

    public void setRegistrationCloseDate(Date registrationCloseDate) {
        this.registrationCloseDate = registrationCloseDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PublishStatus getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(PublishStatus publishStatus) {
        this.publishStatus = publishStatus;
    }

    public Date getPublishStartDate() {
        return publishStartDate;
    }

    public void setPublishStartDate(Date publishStartDate) {
        this.publishStartDate = publishStartDate;
    }

    public Date getPublishEndDate() {
        return publishEndDate;
    }

    public void setPublishEndDate(Date publishEndDate) {
        this.publishEndDate = publishEndDate;
    }
    
    
    public boolean isRegistrationOpened() {
		return registrationOpened;
	}

	public void setRegistrationOpened(boolean registrationOpened) {
		this.registrationOpened = registrationOpened;
	}

	public boolean isProgramOpened() {
		return programOpened;
	}

	public void setProgramOpened(boolean programOpened) {
		this.programOpened = programOpened;
	}

	@Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Publish{");
        sb.append("id=").append(id);
        sb.append(", programStatus=").append(programStatus);
        sb.append(", registrationOpenDate=").append(registrationOpenDate);
        sb.append(", registrationCloseDate=").append(registrationCloseDate);
        sb.append(", publishStatus=").append(publishStatus);
        sb.append(", publishStartDate=").append(publishStartDate);
        sb.append(", publishEndDate=").append(publishEndDate);
        sb.append('}');
        return sb.toString();
    }
}
