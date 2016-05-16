package net.cachapa.businesstime.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.cachapa.businesstime.model.TimeEvent;

import java.util.ArrayList;
import java.util.List;

public class TimeDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "worktimer.db";
    private static final String TABLE_EVENTS = "'events'";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_AT_WORK = "atwork";
    private static final int DATABASE_VERSION = 1;

    public TimeDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_EVENTS + " (" +
                COL_TIMESTAMP + " TIMESTAMP PRIMARY KEY," +
                COL_AT_WORK + " BOOLEAN NOT NULL" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing
    }

    public void insertEvent(long timestamp, boolean atWork) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COL_TIMESTAMP, timestamp);
        cv.put(COL_AT_WORK, atWork);

        db.insert(TABLE_EVENTS, null, cv);
    }

    public List<TimeEvent> getEvents() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_EVENTS + " ORDER BY " + COL_TIMESTAMP + " ASC";

        ArrayList<TimeEvent> values = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            values.add(readEvent(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return values;
    }

    public List<TimeEvent> getEvents(long from, long to) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_EVENTS + " WHERE " + COL_TIMESTAMP + " > " + from + " AND " + COL_TIMESTAMP + " < " + to + " ORDER BY " + COL_TIMESTAMP + " ASC";

        ArrayList<TimeEvent> values = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            values.add(readEvent(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return values;
    }

    public List<Long> getWorkdays() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT DISTINCT STRFTIME('%s', DATE(" + COL_TIMESTAMP + " / 1000, 'unixepoch', 'localtime'),'+12 HOURS') * 1000 FROM " + TABLE_EVENTS;

        ArrayList<Long> values = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            values.add(cursor.getLong(0));
            cursor.moveToNext();
        }
        cursor.close();
        return values;
    }

    public int getWorkdayCount() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT COUNT(DISTINCT DATE(" + COL_TIMESTAMP + "/1000, 'unixepoch', 'localtime')) FROM " + TABLE_EVENTS;

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public TimeEvent getLastEvent() {
        return getEventBefore(System.currentTimeMillis());
    }

    public TimeEvent getEventBefore(long timestamp) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_EVENTS +
                " WHERE " + COL_TIMESTAMP + " < " + timestamp +
                " ORDER BY " + COL_TIMESTAMP + " DESC LIMIT 1";

        Cursor cursor = db.rawQuery(sql, null);
        TimeEvent event = null;

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            event = readEvent(cursor);
            cursor.close();
        }

        return event;
    }

    public void delete(long timestamp) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_EVENTS + " WHERE " + COL_TIMESTAMP + " = " + timestamp);
    }

    public void deleteAllValues() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE " + TABLE_EVENTS);
        onCreate(db);
    }

    private TimeEvent readEvent(Cursor cursor) {
        long timestamp = cursor.getLong(0);
        boolean atWork = cursor.getInt(1) > 0;

        return new TimeEvent(timestamp, atWork);
    }
}
