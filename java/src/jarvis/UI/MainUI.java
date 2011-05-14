package jarvis.UI;

import java.awt.Toolkit;
import java.net.MalformedURLException;
import javax.swing.JFrame;

public class MainUI extends JFrame
{
    private static final long serialVersionUID = 1L;

    public MainUI() throws InterruptedException, MalformedURLException
    {
        super("Jarvis Main UI");
        
        MainPanel mp = new MainPanel();
        add(mp);
        
        Toolkit tk = Toolkit.getDefaultToolkit();  
        int xSize = ((int) tk.getScreenSize().getWidth());  
        int ySize = ((int) tk.getScreenSize().getHeight());  
        setSize(xSize, ySize);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    public static void main(String[] args) throws MalformedURLException, InterruptedException
    {
        new MainUI();
    }
}
