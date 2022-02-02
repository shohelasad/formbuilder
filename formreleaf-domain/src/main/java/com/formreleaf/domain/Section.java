package com.formreleaf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.formreleaf.validation.ValidateDateRange;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@ValidateDateRange(start = "openDate", end = "closeDate", message = "End Date appears to be before Start Date")
public class Section extends BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty
    @Size(max = 200)
    private String name;

    @NotNull
    private Date openDate;

    @NotNull
    private Date closeDate;

    @Size(max = 10)
    private String sectionCode;

    @Size(max = 10)
    private String price;

    private Integer spaceLimit;

    @Size(max = 20)
    private String meetingTime;

    @JsonIgnore
    @ManyToOne
    private Program program;

    @Transient
    private Boolean selected;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getSpaceLimit() {
        return spaceLimit;
    }

    public void setSpaceLimit(Integer spaceLimit) {
        this.spaceLimit = spaceLimit;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Boolean isSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) &&
                Objects.equals(name, section.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Section{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", openDate=").append(openDate);
        sb.append(", closeDate=").append(closeDate);
        sb.append(", sectionCode='").append(sectionCode).append('\'');
        sb.append(", price='").append(price).append('\'');
        sb.append(", spaceLimit=").append(spaceLimit);
        sb.append(", meetingTime='").append(meetingTime).append('\'');
        sb.append(", program=").append(program);
        sb.append('}');
        return super.toString() + sb.toString();
    }
}
