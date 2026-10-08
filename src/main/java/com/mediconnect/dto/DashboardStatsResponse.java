package com.mediconnect.dto;

public class DashboardStatsResponse {
    private long totalAppointments;
    private long upcomingVisits;
    private long totalDoctors;
    private long totalRecords;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(long totalAppointments, long upcomingVisits, long totalDoctors, long totalRecords) {
        this.totalAppointments = totalAppointments;
        this.upcomingVisits = upcomingVisits;
        this.totalDoctors = totalDoctors;
        this.totalRecords = totalRecords;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public long getUpcomingVisits() {
        return upcomingVisits;
    }

    public void setUpcomingVisits(long upcomingVisits) {
        this.upcomingVisits = upcomingVisits;
    }

    public long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(long totalRecords) {
        this.totalRecords = totalRecords;
    }
}
