package PriorityPilot;

import java.time.LocalTime;

public class Settings {
    //The settings class contains the frequency of how offten the user will be notified of tasks.
    //The frequency is the number of notifications within one day
    private int notificationFrequency;
    private LocalTime notificationStart;
    private LocalTime notificationEnd;
    
    Settings(int frequency, LocalTime notificationStart, LocalTime notificationEnd)
    {
        this.notificationFrequency = frequency;
        this.notificationStart = notificationStart;
        this.notificationEnd = notificationEnd;
    }
    
    public void setNotiFreq(int frequency)
    {
        this.notificationFrequency = frequency;
    }
    
    public int getNotiFreq()
    {
        return this.notificationFrequency;
    }
    
    public void setNotStartTime(LocalTime startTime)
    {
        this.notificationStart = startTime;
    }
    
    public LocalTime getNotStartTime()
    {
        return this.notificationStart;
    }
    
    public void setNotEndTime(LocalTime endTime)
    {
        this.notificationEnd = endTime;
    }
    
    public LocalTime getNotEndTime()
    {
        return this.notificationEnd;
    }
}