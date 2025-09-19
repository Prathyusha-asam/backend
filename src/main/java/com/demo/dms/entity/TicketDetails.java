package com.demo.dms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TICKET_DETAILS", uniqueConstraints = {
        @UniqueConstraint(name = "UK_TICKET_DETAILS_TN", columnNames = "TICKET_NUMBER")
})
public class TicketDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long Id;

    @Column(name = "TICKET_NUMBER", nullable = false, unique = true, length = 50)
    @NotBlank(message = "ticketNumber is required")
    @Size(max = 50, message = "ticketNumber must be ≤ 50 chars")
    private String ticketNumber;

    @Column(name = "TICKET_TYPE")
    private String ticketType;

    @Column(name = "DEV_TYPE")
    private String devType;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "ESTIMATION")
    private long estimation;

    @Column(name = "IS_RETURNED")
    private boolean returned;

    @Column(name = "RETURN_NUMBER")
    private long returnNumber;

    @Column(name = "ASSIGNEE")
    private String assignee;

    @Column(name = "START_DATE")
    private String startDate;

    @Column(name = "COMPLETE_DATE")
    private String completeDate;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_ON")
    private String createdOn;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_ON")
    private String updatedOn;

    @PrePersist @PreUpdate
    void normalize() {
        if (ticketNumber != null) ticketNumber = ticketNumber.trim();
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getEstimation() {
        return estimation;
    }

    public void setEstimation(long estimation) {
        this.estimation = estimation;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public long getReturnNumber() {
        return returnNumber;
    }

    public void setReturnNumber(long returnNumber) {
        this.returnNumber = returnNumber;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
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

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }
}
