package net.cachapa.businesstime.model;

public class CheckedTimeEvent extends TimeEvent {
    public CheckedTimeEvent(long timestamp, boolean atWork) {
        super(timestamp, atWork);
    }
}
