package com.demo.dms.web.dto;

public class WeeklyTrendPoint {
    private String period;
    private int total;
    private int returned;

    public WeeklyTrendPoint() {}

    public WeeklyTrendPoint(String period, int total, int returned) {
        this.period = period;
        this.total = total;
        this.returned = returned;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getReturned() {
        return returned;
    }

    public void setReturned(int returned) {
        this.returned = returned;
    }
}
