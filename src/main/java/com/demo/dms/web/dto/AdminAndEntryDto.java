package com.demo.dms.web.dto;

import com.demo.dms.entity.ChildEntryDetails;
import com.demo.dms.entity.TicketDetailsEntry;
import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
public class AdminAndEntryDto {
    String ticketNumber;
    String ticketType;
    String devType;
    LocalDateTime createdOn;
    String createdBy;
    LocalDateTime updatedOn;
    String updatedBy;
    boolean returned;
    int returnedNumber;
    List<ChildEntryDetails> entries;

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

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public List<ChildEntryDetails> getEntries() {
        return entries;
    }

    public void setEntries(List<ChildEntryDetails> entries) {
        this.entries = entries;
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
