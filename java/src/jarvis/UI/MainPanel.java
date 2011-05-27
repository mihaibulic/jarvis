package jarvis.UI;

import jarvis.music.MusicPanel;
import jarvis.sleep.AlarmClock;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

public class MainPanel extends JTabbedPane
{
    private static final long serialVersionUID = 1L;

    private JComponent schedulePanel;
    private JComponent mediaPanel;
    private JComponent housePanel;
    private JComponent commPanel;

    private static final int keyEventMaskKey = KeyEvent.CTRL_MASK; // mask needed to active tab switching
    
    // first key in sequence to set visible tab (ie VK_1 corresponds to number key 1 for tab 0, number key 2 for tab 1, key 3 for tab 2, etc.)
    private static final int keyEventBaseKey = KeyEvent.VK_1; 
    
    private static final KeyStroke NEXT_HOTKEY = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_MASK);
    private static final KeyStroke PREV_HOTKEY = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_MASK);
    
    private String musicURL = System.getenv("HOME")+"/Music"; // FIXME make this less hardcoded
    
    public MainPanel()
    {
        GlobalHotkeyManager manager = GlobalHotkeyManager.getInstance();

        schedulePanel = new AlarmClock();
        mediaPanel = new MusicPanel(musicURL);
        housePanel = new JLabel("house");
        commPanel  = new JLabel("communications");
        
        add("Schedule", schedulePanel);
        add("Media", mediaPanel);
        add("House", housePanel);
        add("Communication", commPanel);

        addShortcuts(manager, keyEventBaseKey, keyEventMaskKey);
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
    
    private void addShortcut(GlobalHotkeyManager manager, KeyStroke stroke, final int tabIndex)
    {
        Action tab = new AbstractAction() 
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) 
            {
                setSelectedIndex(tabIndex);
            }
        };
        
        manager.getInputMap().put(stroke, tab.toString());
        manager.getActionMap().put(tab.toString(), tab);
    }

    private void addShortcut(GlobalHotkeyManager manager, KeyStroke stroke, Action action)
    {
        manager.getInputMap().put(stroke, action.toString());
        manager.getActionMap().put(action.toString(), action);
    }
    
    private void addShortcuts(GlobalHotkeyManager manager, int keyEventbaseKey, int keyEventMaskKey)
    {
        addShortcut(manager, NEXT_HOTKEY, createAction(true));
        addShortcut(manager, PREV_HOTKEY, createAction(false));
        
        for(int x = 0; x < getTabCount(); x++)
        {
            addShortcut(manager, KeyStroke.getKeyStroke(keyEventbaseKey + x, keyEventMaskKey), x);
        }
    }
}
