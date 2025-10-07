package com.demo.dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "`Ticket_Return`")
@NoArgsConstructor
@AllArgsConstructor
public class TicketReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`Id`")
    private long id;

    @Column(name = "`Ticket_Entry_Id`")
    private long ticketEntryId;

    @Column(name = "`Ticket_Number`")
    private String ticketNumber;

    @Column(name = "`Returned_By`")
    private String returnedBy;

    @Column(name = "`Return_Comment`")
    private String returnComment;

    @Column(name = "`Created_On`")
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
