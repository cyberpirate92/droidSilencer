package com.raviteja.silencer;

import java.util.Calendar;

/**
 * Created by raviteja on 02-09-2015.
 */
public class SilenceEvent {

    private Calendar silenceFrom,silenceTo;
    private String description;

    public SilenceEvent() {

    }

    public SilenceEvent(Calendar f,Calendar t,String d) {
        this.silenceFrom = f;
        this.silenceTo = t;
        this.description = d;
    }

    public int getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(int uniqueID) {
        this.uniqueID = uniqueID;
    }

    private int uniqueID;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getSilenceTo() {
        return silenceTo;
    }

    public void setSilenceTo(Calendar silenceTo) {
        this.silenceTo = silenceTo;
    }

    public Calendar getSilenceFrom() {
        return silenceFrom;
    }

    public void setSilenceFrom(Calendar silenceFrom) {
        this.silenceFrom = silenceFrom;
    }

    public String getDateString()
    {
        if(silenceFrom.get(Calendar.DAY_OF_MONTH) == silenceTo.get(Calendar.DAY_OF_MONTH) && silenceFrom.get(Calendar.MONTH) == silenceTo.get(Calendar.MONTH) && silenceFrom.get(Calendar.YEAR) == silenceTo.get(Calendar.YEAR))
        {
            int day,month,year;
            day = silenceFrom.get(Calendar.DAY_OF_MONTH);
            month = silenceFrom.get(Calendar.MONTH);
            year = silenceFrom.get(Calendar.YEAR);
            return day + " " + getMonth(month) + " " + year;
        }
        else if(silenceFrom.get(Calendar.MONTH) == silenceTo.get(Calendar.MONTH) && silenceFrom.get(Calendar.YEAR) == silenceTo.get(Calendar.YEAR))
        {
            int day1,day2,month,year;
            day1 = silenceFrom.get(Calendar.DAY_OF_MONTH);
            day2 = silenceTo.get(Calendar.DAY_OF_MONTH);
            month = silenceFrom.get(Calendar.MONTH);
            year = silenceFrom.get(Calendar.YEAR);
            return day1 + " - " + day2 + " " + getMonth(month) + " " + year;
        }
        else
        {
           return "ERR";
        }
    }

    public String getTimeString()
    {
        int h1,h2,m1,m2;
        h1 = silenceFrom.get(Calendar.HOUR_OF_DAY);
        h2 = silenceTo.get(Calendar.HOUR_OF_DAY);
        m1 = silenceFrom.get(Calendar.MINUTE);
        m2 = silenceTo.get(Calendar.MINUTE);
        return format(h1) + ":"+format(m1) + " - " + format(h2) + ":"+ format(m2);
    }

    public static String format(int n) // returns a 0-padded string representation of a number
    {
        if(n >= 0 && n <= 9)
            return "0"+n;
        else
            return ""+n;
    }
    public static String getMonth(int month)
    {
        String month_names[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        if(month < month_names.length)
            return month_names[month];
        else
            return month + "";
    }
    public boolean equals(SilenceEvent event) {
        if(this.silenceFrom.getTimeInMillis() == event.getSilenceFrom().getTimeInMillis()) {
            if(this.silenceTo.getTimeInMillis() == event.getSilenceTo().getTimeInMillis()) {
                return true;
            }
        }
        return false;
    }
    public boolean strictlyEquals(SilenceEvent event) {
        if(this.equals(event)) {
            return this.description.equals(event.getDescription());
        }
        return false;
    }
}
