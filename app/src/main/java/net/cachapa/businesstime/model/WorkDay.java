package net.cachapa.businesstime.model;

public class WorkDay {
    private long mDate;
    private long mWorkTime;
    private long mPauseTime;
    private long mEnterTime;
    private long mLeaveTime;
    private int mEventCount;

    public WorkDay(long date, long workTime, long pauseTime, long enterTime, long leaveTime, int eventCount) {
        mDate = date;
        mWorkTime = workTime;
        mPauseTime = pauseTime;
        mEnterTime = enterTime;
        mLeaveTime = leaveTime;
        mEventCount = eventCount;
    }

    public long getDate() {
        return mDate;
    }

    public long getWorkTime() {
        return mWorkTime;
    }

    public long getPauseTime() {
        return mPauseTime;
    }

    public long getEnterTime() {
        return mEnterTime;
    }

    public long getLeaveTime() {
        return mLeaveTime;
    }

    public int getEventCount() {
        return mEventCount;
    }
}
