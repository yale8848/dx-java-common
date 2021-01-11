package com.daoxuehao.java.dxcommon.logback;

/**
 * Yale
 * create at: 2021-01-11 11:37
 **/
public class TimeRecord {

    int timeWindow;

    long lastTime;

    public static TimeRecord create(int timeWindow){
        TimeRecord timeRecord = new TimeRecord();
        timeRecord.setTimeWindow(timeWindow);
        return timeRecord;
    }

    protected long getLastTime() {
        return lastTime;
    }

    protected void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public int getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(int timeWindow) {
        this.timeWindow = timeWindow;
    }

}
