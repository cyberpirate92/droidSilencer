package com.raviteja.silencer;

/**
 * Created by CYBERZEUS on 02-09-2015.
 */
public class SilenceEvent {

    private String date;
    private String fromTime;
    private String toTime;
    private String description;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateString()
    {
        String[] parts = this.date.split("-");
        if(parts.length != 3){
            return this.date;
        }

        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        String monthNames[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        return day+" "+monthNames[month]+" "+year;
    }

    public String getFullTimeString() {
        return getTimeString(this.fromTime) + " - "+getTimeString(this.toTime);
    }

    private String getTimeString(String time)
    {
        String parts[] = time.split(":");
        int h,m;
        if(parts.length != 2){
            return time;
        }
        h = Integer.parseInt(parts[0]);
        m = Integer.parseInt(parts[1]);
        return format(h) + ":" + format(m);
    }

    private String format(int n) // returns a 0-padded string representation of a number
    {
        if(n >= 0 && n <= 9)
            return "0"+n;
        else
            return ""+n;
    }
}
