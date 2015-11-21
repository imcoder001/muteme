package com.technospaces.locationapp;

import java.io.Serializable;

/**
 * Created by Coder on 9/12/2015.
 */
public class TimeRange implements Serializable {
    private int _id;
    private String name;
    private String timeStart;
    private String timeEnd;
    private String alarmIds;

    public String getName() {
        return name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getAlarmIds() {
        return alarmIds;
    }

    public void setAlarmIds(String alarmIds) {
        this.alarmIds = alarmIds;
    }

    @Override
    public String toString() {
        String s = "Name: "+this.name+ "\nStarting Time: "+this.timeStart+ "\nEnding Time: "+this.timeEnd;
        return s;
    }
}
