package net.cachapa.businesstime.manager;

import android.content.Context;
import android.text.format.DateUtils;

import net.cachapa.businesstime.model.TimeEvent;
import net.cachapa.businesstime.model.WorkDay;
import net.cachapa.businesstime.repository.TimeDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class TimeManager {
    private TimeDatabase mTimeDatabase;
    private Calendar mCalendar;
    private List<OnTimeListener> mTimeListeners;

    private static TimeManager instance;
    
    public static TimeManager getInstance(Context context) {
        if (instance == null) {
            instance = new TimeManager(context.getApplicationContext());
        }
        return instance;
    }

    private TimeManager(Context context) {
        mTimeDatabase = new TimeDatabase(context);

        mCalendar = GregorianCalendar.getInstance();

        mTimeListeners = new LinkedList<>();
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
        insertEvent(System.currentTimeMillis(), true);
    }

    public void stopWork() {
        insertEvent(System.currentTimeMillis(), false);
    }

    public void insertEvent(long timestamp, boolean atWork) {
        if (isAtWork(timestamp) == atWork) {
            return;
        }

        mTimeDatabase.insertEvent(timestamp, atWork);
        notifyNewEvent();
    }

    public void removeEvent(long timestamp) {
        mTimeDatabase.delete(timestamp);
        notifyNewEvent();
    }

    public void deleteDatabase() {
        mTimeDatabase.deleteAllValues();
    }

    public boolean isAtWork(long timestamp) {
        TimeEvent event = mTimeDatabase.getEventBefore(timestamp);
        return event != null && event.isAtWork();
    }

    public long getWorkTimeToday() {
        return getWorkTimeAtDay(System.currentTimeMillis());
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

        long connectedTime = 0L;
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
            return new WorkDay(date, 0L, 0L, date, date);
        }

        long workTime = 0L;
        long pauseTime = 0L;

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
        long dailyWorkTime = 8 * DateUtils.HOUR_IN_MILLIS;
        return getTotalWorkTime() - dailyWorkTime * mTimeDatabase.getWorkdayCount();
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
        void onTimeModified();
    }
}
