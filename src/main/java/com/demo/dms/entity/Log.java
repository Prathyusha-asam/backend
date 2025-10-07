package com.demo.dms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`LOGS`")
@NoArgsConstructor
@AllArgsConstructor
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`LOG_ID`")
    private long logId;

    @Column(name = "`MODIFIED_ON`")
    private String modifiedDate;

    @Column(name = "`DATA`")
    private String data;

    @Column(name = "`ACTION`")
    private String action;

    @Column(name = "`MODIFIED_BY`")
    private String modifiedBy;

    @Column(name = "`TICKET_NUMBER`", nullable = false, unique = true, length = 50)
    @NotBlank(message = "ticketNumber is required")
    @Size(max = 50, message = "ticketNumber must be ≤ 50 chars")
    private String ticketNumber;

    @Column(name = "`CREATED_BY`")
    private String createdBy;

    @Column(name = "`CREATED_ON`")
    private String createdOn;

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
