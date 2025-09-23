package com.demo.dms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Ticket_Details_Entry")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketDetailsEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    // Association to parent via unique Ticket_Number (NOT the PK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "Ticket_Number",
        referencedColumnName = "Ticket_Number",
        foreignKey = @ForeignKey(name = "FK_TDE_PARENT_TICKET"),
        nullable = false
    )
    private ParentTicketDetails ticket;

    @Column(name = "Dev_Type", nullable = false, length = 50)
    private String devType;

    @Column(name = "Assignee", length = 255)
    private String assignee;

    @Column(name = "Estimation", length = 50)
    private long estimation;

    @Column(name = "Status", length = 50)
    private String status;

    @Column(name = "Start_Date", length = 50)
    private String startDate;

    @Column(name = "End_Date", length = 50)
    private String endDate;

    @Column(name = "Created_By", length = 255)
    private String createdBy;

    @Column(name = "Created_On", insertable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "Updated_By", length = 255)
    private String updatedBy;

    @Column(name = "Updated_On", insertable = false, updatable = false)
    private LocalDateTime updatedOn;

    @Column(name = "Comments")
    private String comments;

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
}
