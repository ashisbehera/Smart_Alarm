package com.coffeecoders.smartalarm.calender;

public class Events {
    private String event_name = null;
    private String event_s_time = null;
    private String event_e_time = null;
    private long s_time = 0;
    private long e_time = 0;

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_s_time() {
        return event_s_time;
    }

    public void setEvent_s_time(String event_s_time) {
        this.event_s_time = event_s_time;
    }

    public String getEvent_e_time() {
        return event_e_time;
    }

    public void setEvent_e_time(String event_e_time) {
        this.event_e_time = event_e_time;
    }

    public long getS_time() {
        return s_time;
    }

    public void setS_time(long s_time) {
        this.s_time = s_time;
    }

    public long getE_time() {
        return e_time;
    }

    public void setE_time(long e_time) {
        this.e_time = e_time;
    }
}
