package com.demo.dms.web.dto;

public class TicketStatus {
    private String status;
    private long statusCount;

    public TicketStatus() {
    }

    public TicketStatus(String status, int statusCount) {
        this.status = status;
        this.statusCount = statusCount;
    }

    public long getStatusCount() {
        return statusCount;
    }

    public void setStatusCount(long statusCount) {
        this.statusCount = statusCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
