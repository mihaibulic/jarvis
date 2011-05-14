package jarvis.sleep;

import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

public class AlarmPopUp extends JPanel
{
    private static final long serialVersionUID = 1L;
    private JSpinner eventTime;
    private Object[] options;
    
    public static final int APPLY = 0;
    public static final int CANCEL = 1;
    public static final int DELETE = 2;
    
    public AlarmPopUp(Date date)
    {
        Calendar calendar = Calendar.getInstance();
        Date current = calendar.getTime();
        calendar.add(Calendar.YEAR, 5);
        Date future = calendar.getTime();
        
        eventTime = new JSpinner(new SpinnerDateModel(date, current, future, Calendar.YEAR));
        eventTime.setEditor(new JSpinner.DateEditor(eventTime, "MM/dd/yyyy HH:mm"));
        
        add(eventTime);
        
        options = new Object[3];
        options[APPLY] = "Apply";
        options[CANCEL] = "Cancel";
        options[DELETE] = "Delete";
    }

    public int popUp()
    {
        return JOptionPane.showOptionDialog(this,"Edit time of this alarm or delete it",
                "Alarm Edit",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[1]); //default button title
    }
    
    public Date getNewDate()
    {
        return ((SpinnerDateModel) eventTime.getModel()).getDate();
    }
}
