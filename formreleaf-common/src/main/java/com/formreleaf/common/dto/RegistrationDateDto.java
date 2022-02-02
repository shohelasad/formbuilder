package com.formreleaf.common.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Bazlur Rahman Rokon
 * @date 7/10/15.
 */
@JsonPropertyOrder({"startDate", "endDate"})
public class RegistrationDateDto implements Serializable {
    private Date startDate;
    private Date endDate;

    public Date getStartDate() {
        return startDate;
    }

    public RegistrationDateDto setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public RegistrationDateDto setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }
}
