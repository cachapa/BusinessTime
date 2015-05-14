package com.codingbuffalo.businesstime.manager;

import android.content.Context;
import android.text.format.DateUtils;

import com.codingbuffalo.businesstime.repository.TimeDatabase;
import com.codingbuffalo.businesstime.model.TimeEvent;
import com.codingbuffalo.businesstime.model.WorkDay;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class TimeManager {
	private static TimeManager instance;

	public static TimeManager getInstance(Context context) {
		if (instance == null) {
			instance = new TimeManager(context);
		}
		return instance;
	}

	private TimeDatabase         mTimeDatabase;
	private Calendar             mCalendar;
	private List<OnTimeListener> mTimeListeners;

	private boolean mCacheAtWork;
	private long    mCacheToday;
	private long    mCacheBalance;
	private long    mCacheUpdate;

	private TimeManager(Context context) {
		mTimeDatabase = new TimeDatabase(context);

		mCalendar = GregorianCalendar.getInstance();

		mTimeListeners = new LinkedList<>();

		TimeEvent lastEvent = mTimeDatabase.getLastEvent();
		mCacheAtWork = lastEvent != null && lastEvent.isAtWork();

		updateCache();
	}

	private void updateCache() {
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		mCalendar.set(Calendar.HOUR_OF_DAY, 0);
		mCalendar.set(Calendar.MINUTE, 0);
		mCalendar.set(Calendar.SECOND, 0);
		mCalendar.set(Calendar.MILLISECOND, 0);
		long from = mCalendar.getTimeInMillis();
		long to = System.currentTimeMillis();

		mCacheToday = getWorkTime(from, to);

		long dailyWorkTime = 8 * DateUtils.HOUR_IN_MILLIS;
		mCacheBalance = getTotalWorkTime() - dailyWorkTime * mTimeDatabase.getWorkdayCount();

		mCacheUpdate = System.currentTimeMillis();
	}

	public List<TimeEvent> getEvents() {
		return mTimeDatabase.getEvents();
	}

	public List<TimeEvent> getEventsForDay(long date) {
		mCalendar.setTimeInMillis(date);
		mCalendar.set(Calendar.HOUR_OF_DAY, 0);
		mCalendar.set(Calendar.MINUTE, 0);
		mCalendar.set(Calendar.SECOND, 0);
		mCalendar.set(Calendar.MILLISECOND, 0);
		long from = mCalendar.getTimeInMillis();

		mCalendar.add(Calendar.DAY_OF_MONTH, 1);
		long to = Math.min(mCalendar.getTimeInMillis(), System.currentTimeMillis());

		return mTimeDatabase.getEvents(from, to);
	}

	public void startWork() {
		if (!mCacheAtWork) {
			insertEvent(System.currentTimeMillis(), true);
		}
	}

	public void stopWork() {
		if (mCacheAtWork) {
			insertEvent(System.currentTimeMillis(), false);
		}
	}

	public void insertEvent(long timestamp, boolean atWork) {
		mTimeDatabase.insertEvent(timestamp, atWork);
		mCacheAtWork = atWork;

		updateCache();

		notifyNewEvent();
	}

	public void deleteDatabase() {
		mTimeDatabase.deleteAllValues();

		mCacheAtWork = false;
		mCacheUpdate = 0l;
		mCacheToday = 0l;
		mCacheBalance = 0;
	}

	public boolean isCacheAtWork() {
		return mCacheAtWork;
	}

	public long getWorkTimeToday() {
		return mCacheToday + (mCacheAtWork ? (System.currentTimeMillis() - mCacheUpdate) : 0);
	}

	public long getTotalWorkTime() {
		return getWorkTime(0, System.currentTimeMillis());
	}

	public long getWorkTimeAtDay(long time) {
		mCalendar.setTimeInMillis(time);
		mCalendar.set(Calendar.HOUR_OF_DAY, 0);
		mCalendar.set(Calendar.MINUTE, 0);
		mCalendar.set(Calendar.SECOND, 0);
		mCalendar.set(Calendar.MILLISECOND, 0);
		long from = mCalendar.getTimeInMillis();

		mCalendar.add(Calendar.DAY_OF_MONTH, 1);
		long to = Math.min(mCalendar.getTimeInMillis(), System.currentTimeMillis());

		return getWorkTime(from, to);
	}

	public long getWorkTime(long from, long to) {
		List<TimeEvent> events = mTimeDatabase.getEvents(from, to);

		long connectedTime = 0l;
		TimeEvent previousEvent;
		TimeEvent lastEvent = mTimeDatabase.getEventBefore(from);
		if (lastEvent != null) {
			previousEvent = new TimeEvent(from, lastEvent.isAtWork());
		} else {
			previousEvent = new TimeEvent(from, false);
		}

		for (TimeEvent event : events) {
			if (event.isAtWork() == previousEvent.isAtWork()) {
				continue;
			}

			if (!event.isAtWork()) {
				connectedTime += (event.getTimestamp() - previousEvent.getTimestamp());
			}
			previousEvent = event;
		}

		if (previousEvent.isAtWork()) {
			connectedTime += (to - previousEvent.getTimestamp());
		}

		return connectedTime;
	}

	public WorkDay getWorkDay(long date) {
		mCalendar.setTimeInMillis(date);
		mCalendar.set(Calendar.HOUR_OF_DAY, 0);
		mCalendar.set(Calendar.MINUTE, 0);
		mCalendar.set(Calendar.SECOND, 0);
		mCalendar.set(Calendar.MILLISECOND, 0);
		long from = mCalendar.getTimeInMillis();

		mCalendar.add(Calendar.DAY_OF_MONTH, 1);
		long to = Math.min(mCalendar.getTimeInMillis(), System.currentTimeMillis());

		List<TimeEvent> events = mTimeDatabase.getEvents(from, to);
		if (events.isEmpty()) {
			return new WorkDay(date, 0l, 0l, date, date);
		}

		long workTime = 0l;
		long pauseTime = 0l;

		TimeEvent previousEvent;
		TimeEvent lastEvent = mTimeDatabase.getEventBefore(from);
		if (lastEvent != null) {
			previousEvent = new TimeEvent(from, lastEvent.isAtWork());
		} else {
			previousEvent = new TimeEvent(from, false);
		}
		boolean startedWork = previousEvent.isAtWork();

		long enterTime = startedWork ? from : events.get(0).getTimestamp();
		TimeEvent finalEvent = events.get(events.size() - 1);
		long leaveTime = finalEvent.isAtWork() ? to : finalEvent.getTimestamp();

		for (TimeEvent event : events) {
			// Ignore repeated events
			if (event.isAtWork() == previousEvent.isAtWork()) {
				continue;
			}

			if (event.isAtWork()) {
				if (startedWork) {
					pauseTime += (event.getTimestamp() - previousEvent.getTimestamp());
				}
			} else {
				workTime += (event.getTimestamp() - previousEvent.getTimestamp());
				startedWork = true;
			}
			previousEvent = event;
		}

		if (previousEvent.isAtWork()) {
			workTime += (to - previousEvent.getTimestamp());
		}

		return new WorkDay(from + 12 * DateUtils.HOUR_IN_MILLIS, workTime, pauseTime, enterTime, leaveTime);
	}

	public long getTimeBalance() {
		return mCacheBalance + (mCacheAtWork ? (System.currentTimeMillis() - mCacheUpdate) : 0);
	}

	public List<Long> getWorkDays() {
		return mTimeDatabase.getWorkdays();
	}

	public void addOnTimeListener(OnTimeListener listener) {
		mTimeListeners.add(listener);
	}

	public void removeOnTimeListener(OnTimeListener listener) {
		mTimeListeners.remove(listener);
	}

	/* Callbacks */
	private void notifyNewEvent() {
		for (OnTimeListener listener : mTimeListeners) {
			listener.onTimeModified();
		}
	}

	public interface OnTimeListener {
		public void onTimeModified();
	}
}
