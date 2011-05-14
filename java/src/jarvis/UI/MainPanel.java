package jarvis.UI;

import jarvis.sleep.AlarmClock;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

public class MainPanel extends JTabbedPane
{
    private static final long serialVersionUID = 1L;

    private JComponent alarmPanel;
    private JComponent musicPanel;
    private JComponent housePanel;
    private JComponent commPanel;

    public MainPanel() throws MalformedURLException, InterruptedException
    {
        alarmPanel = new AlarmClock();
        musicPanel = new JLabel("music");
        housePanel = new JLabel("house");
        commPanel  = new JLabel("communications");
        
        add("Schedule", alarmPanel);
        add("Media", musicPanel);
        add("House", housePanel);
        add("Communication", commPanel);

        addShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK),0);
        addShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_MASK),1);
        addShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_MASK),2);
        addShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.CTRL_MASK),3);
        
        addShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_MASK), createAction(true));
        addShortcut(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_MASK), createAction(false));
    }

    private Action createAction(final boolean forward)
    {
        Action action = new AbstractAction() 
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) 
            {
                int direction = forward ? 1 : -1;
                if(getSelectedIndex()+direction < getTabCount() &&
                    getSelectedIndex()+direction >= 0)
                {
                    setSelectedIndex(getSelectedIndex()+direction);
                }
            }
        };
        
        return action;
    }
    
    private void addShortcut(KeyStroke stroke, final int tabIndex)
    {
        Action tab = new AbstractAction() 
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) 
            {
                setSelectedIndex(tabIndex);
            }
        };
        
        getInputMap().put(stroke, tab.toString());
        getActionMap().put(tab.toString(), tab);
    }

    private void addShortcut(KeyStroke stroke, Action action)
    {
        getInputMap().put(stroke, action.toString());
        getActionMap().put(action.toString(), action);
    }
}
