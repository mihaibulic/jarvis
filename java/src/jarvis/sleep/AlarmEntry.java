package jarvis.sleep;

import java.util.Date;
import java.util.Timer;

public class AlarmEntry
{
    private Timer timer;
    private boolean enabled;
    private Date date;

    public AlarmEntry()
    {
        timer = new Timer();
        enabled = false;
    }

    public AlarmEntry(Date date)
    {
        timer = new Timer();
        this.date = date;
        enabled = false;
    }

    public Date getDate()
    {
        return date;
    }
    
    public Timer getTimer()
    {
        return timer;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }
    
    public void setTimer(Timer timer)
    {
        this.timer = timer;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    public void toggle()
    {
        enabled = !enabled;
    }
    
    public String toString()
    {
        return date.toString();
    }

}
