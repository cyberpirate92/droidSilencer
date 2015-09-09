package com.raviteja.silencer;

/**
 * Created by raviteja on 03-09-2015.
 */
class Time
{
    private int hours,minutes;
    public Time(String time)
    {
        String parts[] = time.split(time);
        if(parts.length != 2){
            hours = minutes = 0;
            return;
        }
        hours = Integer.parseInt(parts[0]);
        minutes = Integer.parseInt(parts[1]);
    }

}
