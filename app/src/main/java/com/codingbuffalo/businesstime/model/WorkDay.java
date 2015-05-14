package com.codingbuffalo.businesstime.model;

public class WorkDay {
	private long mDate;
	private long mWorkTime;
	private long mPauseTime;
	private long mEnterTime;
	private long mLeaveTime;

	public WorkDay(long date, long workTime, long pauseTime, long enterTime, long leaveTime) {
		mDate = date;
		mWorkTime = workTime;
		mPauseTime = pauseTime;
		this.mEnterTime = enterTime;
		this.mLeaveTime = leaveTime;
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
}
