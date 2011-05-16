package jarvis.sleep;

import jarvis.music.MusicDecoder;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AlarmClock extends JPanel implements ActionListener, ListSelectionListener
{
    private static final long serialVersionUID = 1L;

    private final int SECOND = 1000; // milliseconds
    private final int MINUTE = SECOND*60; // milliseconds
    private final int HOUR   = MINUTE*60; // milliseconds
    private final int DAY    = HOUR*24; // milliseconds
    
    private final int SNOOZE = 10 * MINUTE; // in milliseconds
    private final int HOURS = 8;
    private final int MINUTES = 00;

    private MusicDecoder mp3;
    
    private JButton addOffset;
    private JButton snooze;
    private JButton stop;
    private JSpinner hours;
    private JSpinner minutes;
    private JSpinner eventTime;
    private JButton add;
    private JList events;
    private DefaultListModel listModel;
    private String labels[] = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
    private JCheckBox[] days;
    private JButton sleep;
    
    ArrayList<AlarmEntry> entries = new ArrayList<AlarmEntry>();
    AlarmEntry mainEntry = new AlarmEntry();

    public AlarmClock()
    {
        super(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        
        addOffset = new JButton("Add Offset Alarm");
        snooze = new JButton("Snooze");
        stop = new JButton("Stop");
        hours = new JSpinner(new SpinnerNumberModel(HOURS,0,23,1));;
        minutes = new JSpinner(new SpinnerNumberModel(MINUTES,0,59,1));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        
        setDate();
        add = new JButton("Add Event");
        constraints.gridwidth=4;
        add(eventTime, constraints);
        constraints.gridx+=4;
        constraints.gridwidth=1;
        add(add, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        add(new JLabel("hours:"), constraints);
        constraints.gridx++;
        add(hours, constraints);
        constraints.gridx++;
        add(new JLabel("min:"), constraints);
        constraints.gridx++;
        add(minutes, constraints);
        constraints.gridx++;
        add(addOffset, constraints);
        

        days = new JCheckBox[labels.length];
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx += 3;
        constraints.gridy = 0;
        for (int x = 0; x < labels.length; x++)
        {
            days[x] = new JCheckBox(labels[x]);
            add(days[x], constraints);
            constraints.gridy++;
        }

        sleep = new JButton("Sleep");
        constraints.gridy -= 5;
        constraints.gridx = 0;
        constraints.gridwidth = 7;
        add(sleep, constraints);
        constraints.gridy++;
        
        listModel = new DefaultListModel();
        events = new JList(listModel);
        events.setVisibleRowCount(5);
        events.setCellRenderer(new CustomListCellRenderer());
        JScrollPane eventsPane = new JScrollPane(events);
        constraints.gridheight = 5;
        add(eventsPane, constraints);
        constraints.gridheight = 1;
        constraints.gridy+=5;

        constraints.gridx = 0;
        add(snooze, constraints);
        constraints.gridy++;
        add(stop, constraints);
        
        addOffset.addActionListener(this);
        snooze.addActionListener(this);
        stop.addActionListener(this);
        add.addActionListener(this);
        sleep.addActionListener(this);
        events.addListSelectionListener(this);
        
        snooze.setVisible(false);
        stop.setVisible(false);
        
        events.addMouseListener(new Mouse());
        
        (new Refresh()).start();
    }
    
    private class Refresh extends Thread
    {
        public void run()
        {
            try
            {
                Thread.sleep(setDate() - System.currentTimeMillis());
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private class Mouse extends MouseAdapter
    {
        public void mouseClicked(MouseEvent click)
        {
            if(entries.size()>0)
            {
                if(click.getClickCount() == 2)
                {
                    Date date = entries.get(events.locationToIndex(click.getPoint())).getDate();
                    AlarmPopUp popup = new AlarmPopUp(date);
                    int option = popup.popUp();
                    
                    switch(option)
                    {
                        case AlarmPopUp.APPLY:
                            Date newDate = popup.getNewDate();
                            entries.get(events.locationToIndex(click.getPoint())).setDate(newDate);
                            startAlarm(entries.get(events.locationToIndex(click.getPoint())));
                            break;
                        case AlarmPopUp.DELETE:
                            stopAlarm(entries.get(events.locationToIndex(click.getPoint())), true, false);
                            break;
                        case AlarmPopUp.CANCEL:
                        default:
                                break;
                    }
                }
                else
                {
                    if (click.getButton() == MouseEvent.BUTTON3) // right click
                    {
                        stopAlarm(entries.get(events.locationToIndex(click.getPoint())), true, false);
                    }
                    else if (click.getButton() == MouseEvent.BUTTON1) // left click
                    {
                        entries.get(events.locationToIndex(click.getPoint())).toggle();
                        repaint();
                    }
                }
            }
        }
    }
    
    private class CustomListCellRenderer extends DefaultListCellRenderer 
    {  
        private static final long serialVersionUID = 1L;

        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus )
        {  
            Component c = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
            
            if ( entries.get(index).isEnabled() ) 
            {
                c.setBackground( Color.green );  
            }  
            else 
            {  
                c.setBackground( Color.white );
            }
            
            return c;  
        }  
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        Object source = event.getSource();

        if (source == addOffset)
        {
            setOffset();
        }
        else if (source == snooze)
        {
            stopAlarm(mainEntry, false, true);
            start(mainEntry, SNOOZE);
            mainEntry.setEnabled(true);
        }
        else if (source == stop)
        {
            stopAlarm(mainEntry, false, true);
        }
        else if (source == add)
        {
            AlarmEntry entry = new AlarmEntry(((SpinnerDateModel) eventTime.getModel()).getDate());
            startAlarm(entry);
        }
        else if (source == sleep)
        {
            setOffset();
            // fetch from google
            setAlarms();
        }
    }
    
    private class Alarm extends TimerTask
    {
        String song = "file://home/april/jarvis/java/test2.mp3"; // XXX
        AlarmEntry entry;
        boolean[] repeat = new boolean[labels.length];
        
        public Alarm(AlarmEntry entry, boolean[] repeat)
        {
            this.entry = entry;
            for (int x = 0; x < labels.length; x++)
            {
                this.repeat[x] = repeat[x];
            }
        }
        
        public Alarm(AlarmEntry entry)
        {
            this.entry = entry;
            for (int x = 0; x < labels.length; x++)
            {
                repeat[x] = days[x].isSelected();
                days[x].setSelected(false);
            }
        }

        public void run()
        {
            listModel.remove(entries.indexOf(entry));
            events = new JList(listModel);
            entries.remove(entry);
            
            if(entry.isEnabled())
            {
                snooze.setVisible(true);
                stop.setVisible(true);

                int day = getDayOfWeek();
                for(int x = 1; x <= labels.length; x++)
                {
                    day = getNextDay(day);
                    if(repeat[day])
                    {
                        Date date = getDate(x);
                        AlarmEntry newEntry = new AlarmEntry(date);
                        start(newEntry,repeat,date);
                        entries.add(newEntry);
                        
                        listModel.addElement(date);
                        events = new JList(listModel);
                        
                        break;
                    }
                }
                
                try
                {
                    play();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        private void play() throws InterruptedException
        {
            mainEntry = entry;

            if (mp3 != null)
            {
                mp3.stop_playback();
            }
            
            mp3 = new MusicDecoder(song);
            mp3.mute();
            mp3.play_playback();

            float max = mp3.getMaxVolume();
            for(float vol = 0; vol <= max; vol+=max/60)
            {
                Thread.sleep(1000);
                mp3.setVolume(vol);
            }        
        }

         private int getDayOfWeek()
        {
            return (Calendar.getInstance()).get(Calendar.DAY_OF_WEEK)-1;
        }
        
         private Date getDate(int days)
         {
             return new Date(System.currentTimeMillis() + DAY*days);
         }

         private int getNextDay(int day)
        {
            return (day < 6) ? day+1 : 0;
        }
        
        private void start(AlarmEntry entry, boolean[] repeat, Date date)
        {
            (entry.getTimer()).schedule(new Alarm(entry, repeat), date);
        }
    }

    private Date getOffsetDate(JSpinner hours, JSpinner minutes)
    {
        int hrs = ((SpinnerNumberModel) hours.getModel()).getNumber().intValue();
        int min = ((SpinnerNumberModel) minutes.getModel()).getNumber().intValue();

        return new Date(System.currentTimeMillis() + (hrs * 60 * 60 * 1000) + (min * 60 * 1000));
    }

    private Date getOffsetDate(int milliseconds)
    {
        return new Date(System.currentTimeMillis() + milliseconds);
    }

    private void setAlarms()
    {
        Date date = (Date)listModel.get(0);
        entries.get(0).setEnabled(false);
        int earliest = 0;
        for(int x = 1; x < entries.size(); x++)
        {
            entries.get(x).setEnabled(false);
            if(date.after((Date)listModel.get(x)))
            {
                date = (Date)listModel.get(x);
                earliest = x;
            }
        }
        entries.get(earliest).setEnabled(true);
        
        repaint();
    }

    private long setDate()
    {
        Calendar calendar = Calendar.getInstance();
        Date minDate = calendar.getTime();
        
        if(calendar.get(Calendar.HOUR_OF_DAY) >= 9)
        {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 00);
        Date initDate = calendar.getTime();
                
        calendar.add(Calendar.YEAR, 5);
        Date latestDate = calendar.getTime();
        
        eventTime = new JSpinner(new SpinnerDateModel(initDate, minDate, latestDate, Calendar.YEAR));
        eventTime.setEditor(new JSpinner.DateEditor(eventTime, "MM/dd/yyyy HH:mm"));
        
        return initDate.getTime();
    }
    
    private void setOffset()
    {
        startAlarm(new AlarmEntry(getOffsetDate(hours, minutes)));
    }

    private void start(AlarmEntry entry, int milliseconds)
    {
        entry.setDate(getOffsetDate(milliseconds));
        startAlarm(entry);
    }

    public void startAlarm(AlarmEntry entry)
    {
        entries.add(entry);
        listModel.addElement(entry.getDate());
        events = new JList(listModel);
        
        (entry.getTimer()).schedule(new Alarm(entry), entry.getDate());
    }

    public void stopAlarm(AlarmEntry entry, boolean stopTimer, boolean stopMusic)
    {
        if(stopTimer)
        {
            listModel.remove(entries.indexOf(entry));
            entries.remove(entry);
            events = new JList(listModel);
            entry.getTimer().cancel();
        }
        
        if(stopMusic)
        {
            snooze.setVisible(false);
            stop.setVisible(false);
            entry.setEnabled(false);
            if (mp3 != null)
            {
                try
                {
                    mp3.stop_playback();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent arg0)
    {}    
}
