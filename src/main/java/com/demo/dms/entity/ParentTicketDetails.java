package com.demo.dms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "Parent_Ticket_Details", schema="DMS_DIRECTORY",
    uniqueConstraints = @UniqueConstraint(name = "UK_PARENT_TICKET_NUMBER", columnNames = "Ticket_Number")
)
@NoArgsConstructor @AllArgsConstructor @Builder
public class ParentTicketDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Ticket_Number", nullable = false, length = 50)
    private String ticketNumber;

    @Column(name = "TicketType", length = 50)
    private String ticketType;

    @Column(name = "Returned", nullable = false)
    private boolean returned;

    @Column(name = "Returned_Number", nullable = false)
    private int returnedNumber;

    @Column(name = "Dev_Type")
    private String devType;

    @Column(name = "Created_By", length = 255)
    private String createdBy;

    // DB sets defaults; mark read-only so JPA doesn’t try to write them.
    @Column(name = "Created_On", insertable = false, updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "Updated_By", length = 255)
    private String updatedBy;

    @Column(name = "Updated_On", insertable = false, updatable = false)
    private LocalDateTime updatedOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }
}
