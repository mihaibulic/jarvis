package jarvis.sandbox;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddRule extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    
    int count = 0;
    
    JTextField find = new JTextField(10);
    JTextField replace = new JTextField(10);
    JButton add = new JButton("add");
    JComboBox type = new JComboBox( new String[] {"Artist", "Genre"});
    JLabel status = new JLabel();
    
    BufferedWriter inArtists;
    BufferedWriter inGenres;

    public AddRule()
    {
        super("Add rules");

        try
        {
            String dir = System.getenv("HOME") + File.separator + "jarvis" + File.separator;
            inArtists = new BufferedWriter(new FileWriter(dir + "artists", true));
            inGenres = new BufferedWriter(new FileWriter(dir + "genres", true));
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        
        add.addActionListener(this);
        status.setPreferredSize(new Dimension(60,10));
        
        JPanel jp = new JPanel(new FlowLayout());
        
        jp.add(type);
        jp.add(find);
        jp.add(replace);
        jp.add(add);
        jp.add(status);
        add(jp);
        pack();
        setVisible(true);
    }
    
    public static void main(String[] args)
    {
        new AddRule();
    }

    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        if(!find.getText().isEmpty() && !replace.getText().isEmpty())
        {
            if(type.getSelectedItem().equals("Artist"))
            {
                try
                {
                    inArtists.write(find.getText() + "->" + replace.getText()+"\n");
                    inArtists.flush();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else if(type.getSelectedItem().equals("Genre"))
            {
                try
                {
                    inGenres.write(find.getText() + "->" + replace.getText()+"\n");
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            
            status.setText("added " + ++count);
            find.setText("");
            replace.setText("");
        }
    }
}
