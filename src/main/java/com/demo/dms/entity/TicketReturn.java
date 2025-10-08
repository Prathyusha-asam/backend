package com.demo.dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_return")
@NoArgsConstructor
@AllArgsConstructor
public class TicketReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "ticket_entry_id")
    private long ticketEntryId;

    @Column(name = "ticket_number")
    private String ticketNumber;

    @Column(name = "returned_by")
    private String returnedBy;

    @Column(name = "return_comment")
    private String returnComment;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getReturnedBy() {
        return returnedBy;
    }

    public void setReturnedBy(String returnedBy) {
        this.returnedBy = returnedBy;
    }

    public String getReturnComment() {
        return returnComment;
    }

    public void setReturnComment(String returnComment) {
        this.returnComment = returnComment;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public long getTicketEntryId() {
        return ticketEntryId;
    }

    public void setTicketEntryId(long ticketEntryId) {
        this.ticketEntryId = ticketEntryId;
    }
}
