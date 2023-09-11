package com.phonecompany.billing.domain.entities;

import java.util.Date;

public class CallRecord {
    private final String phoneNumber;
    private final Date startTime;
    private final Date endTime;

    public CallRecord(String phoneNumber, Date startTime, Date endTime) {
        this.phoneNumber = phoneNumber;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}