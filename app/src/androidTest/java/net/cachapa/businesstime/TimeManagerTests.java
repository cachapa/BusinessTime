package net.cachapa.businesstime;

import android.test.AndroidTestCase;
import android.text.format.DateUtils;

import junit.framework.Assert;

import net.cachapa.businesstime.manager.TimeManager;
import net.cachapa.businesstime.model.WorkDay;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class TimeManagerTests extends AndroidTestCase {
    private TimeManager mTimeManager;
    private Calendar mCalendar;

    @Override
    protected void setUp() throws Exception {
        mTimeManager = TimeManager.getInstance(getContext());
        mCalendar = GregorianCalendar.getInstance();

        mTimeManager.deleteDatabase();

        long time;

        // Normal day - 8h work with 1 interruption
        time = getTime(2015, 0, 26, 8, 0, 0);
        mTimeManager.insertEvent(time, true);
        time = getTime(2015, 0, 26, 12, 0, 0);
        mTimeManager.insertEvent(time, false);
        time = getTime(2015, 0, 26, 13, 0, 0);
        mTimeManager.insertEvent(time, true);
        time = getTime(2015, 0, 26, 17, 0, 0);
        mTimeManager.insertEvent(time, false);

        // Double entry event
        time = getTime(2015, 0, 28, 10, 0, 0);
        mTimeManager.insertEvent(time, true);
        time = getTime(2015, 0, 28, 12, 0, 0);
        mTimeManager.insertEvent(time, true);
        time = getTime(2015, 0, 28, 17, 0, 0);
        mTimeManager.insertEvent(time, false);

        // Insert and remove event
        time = getTime(2015, 0, 28, 11, 0, 0);
        mTimeManager.insertEvent(time, true);
        mTimeManager.removeEvent(time);

        // Work past midnight
        time = getTime(2015, 0, 29, 17, 0, 0);
        mTimeManager.insertEvent(time, true);
        time = getTime(2015, 0, 30, 1, 0, 0);
        mTimeManager.insertEvent(time, false);
    }

    public void testWorkdayCount() throws Throwable {
        List<Long> workDays = mTimeManager.getWorkDays();

        Assert.assertTrue(workDays.size() == 4);
    }

    public void testNormalDay() {
        long expectedWorkTime = 8 * DateUtils.HOUR_IN_MILLIS;
        long expectedPauseTime = 1 * DateUtils.HOUR_IN_MILLIS;
        long expectedEnterTime = getTime(2015, 0, 26, 8, 0, 0);
        long expectedLeaveTime = getTime(2015, 0, 26, 17, 0, 0);

        WorkDay workDay = mTimeManager.getWorkDay(getTime(2015, 0, 26, 12, 0, 0));

        assertEquals(expectedWorkTime, workDay.getWorkTime());
        assertEquals(expectedPauseTime, workDay.getPauseTime());
        assertEquals(expectedEnterTime, workDay.getEnterTime());
        assertEquals(expectedLeaveTime, workDay.getLeaveTime());
    }

    public void testDoubleEntryDay() {
        long expectedTime = 7 * DateUtils.HOUR_IN_MILLIS;

        long time = mTimeManager.getWorkTimeAtDay(getTime(2015, 0, 28, 12, 0, 0));

        assertTrue(time == expectedTime);
    }

    public void testWorkUntilMidnightDay() {
        long expectedTime = 7 * DateUtils.HOUR_IN_MILLIS;

        long time = mTimeManager.getWorkTimeAtDay(getTime(2015, 0, 29, 12, 0, 0));

        assertTrue(time == expectedTime);
    }

    public void testWorkFromMidnightDay() {
        long expectedTime = 1 * DateUtils.HOUR_IN_MILLIS;

        long time = mTimeManager.getWorkTimeAtDay(getTime(2015, 0, 30, 12, 0, 0));

        assertTrue(time == expectedTime);
    }

    public void testNoWorkDay() {
        long expectedTime = 0l;

        long time = mTimeManager.getWorkTimeAtDay(getTime(2015, 0, 27, 12, 0, 0));

        assertTrue(time == expectedTime);
    }

    public void testTotalWorkTime() {
        long expectedTime = 23 * DateUtils.HOUR_IN_MILLIS;

        long time = mTimeManager.getTotalWorkTime();

        assertTrue(time == expectedTime);
    }

    public void testTimeBalance() {
        long shouldWorkTime = 8 * 4 * DateUtils.HOUR_IN_MILLIS;
        long actualWorkTime = 23 * DateUtils.HOUR_IN_MILLIS;
        long expectedTime = actualWorkTime - shouldWorkTime;

        long time = mTimeManager.getTimeBalance();

        assertEquals(expectedTime, time);
    }

    private long getTime(int year, int month, int day, int hour, int minute, int second) {
        mCalendar.set(year, month, day, hour, minute, second);
        mCalendar.set(Calendar.MILLISECOND, 0);
        return mCalendar.getTimeInMillis();
    }
}
