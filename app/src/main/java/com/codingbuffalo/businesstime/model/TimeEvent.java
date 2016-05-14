package com.codingbuffalo.businesstime.model;

public class TimeEvent {
    private long mTimestamp;
    private boolean mAtWork;

    public TimeEvent(long timestamp, boolean atWork) {
        this.mTimestamp = timestamp;
        this.mAtWork = atWork;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public boolean isAtWork() {
        return mAtWork;
    }
}
