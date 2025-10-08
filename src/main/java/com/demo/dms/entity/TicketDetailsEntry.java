package com.demo.dms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_details_entry")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketDetailsEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Association to parent via unique Ticket_Number (NOT the PK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "ticket_number",
        referencedColumnName = "ticket_number",
        foreignKey = @ForeignKey(name = "FK_TDE_PARENT_TICKET"),
        nullable = false
    )
    private ParentTicketDetails ticket;

    @Column(name = "dev_type", nullable = false, length = 50)
    private String devType;

    @Column(name = "assignee", length = 255)
    private String assignee;

    @Column(name = "estimation", length = 50)
    private long estimation;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "start_date", length = 50)
    private String startDate;

    @Column(name = "end_date", length = 50)
    private String endDate;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "created_on", insertable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "updated_by", length = 255)
    private String updatedBy;

    @Column(name = "updated_on", insertable = false, updatable = false)
    private LocalDateTime updatedOn;

    @Column(name = "comments")
    private String comments;

    @Column(name = "returned")
    private boolean returned;

    @Column(name = "returned_Number")
    private int returnedNumber;


    // Convenience accessors if you often need the raw Ticket_Number
    @Transient
    public String getTicketNumber() {
        return ticket != null ? ticket.getTicketNumber() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParentTicketDetails getTicket() {
        return ticket;
    }

    public void setTicket(ParentTicketDetails ticket) {
        this.ticket = ticket;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public long getEstimation() {
        return estimation;
    }

    public void setEstimation(long estimation) {
        this.estimation = estimation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public int getReturnedNumber() {
        return returnedNumber;
    }

    public void setReturnedNumber(int returnedNumber) {
        this.returnedNumber = returnedNumber;
    }
}
