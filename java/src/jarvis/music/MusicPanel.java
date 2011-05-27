package jarvis.music;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class MusicPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    
    private String[] columnNames = {"Artist", "Title", "Rating", "Played", "Genre", "BMP"};
    private final int ARTIST     = 0;
    private final int TITLE      = 1;
    private final int RATING     = 2;
    private final int PLAYED     = 3;
    private final int GENRE      = 4;
    private final int BPM        = 5;
    private int size = 6;
    
    private JTable mainTable;
    private JScrollPane mainScroll;
    
    private ArrayList<String[]> mainTableEntries = new ArrayList<String[]>();
    private ArrayList<Tag> tags = new ArrayList<Tag>();
    private HashMap<String[], File> songs = new HashMap<String[], File>();

    private MusicDecoder decoder;
    
    public MusicPanel(String url)
    {
        int xSize = ((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth());
        int ySize = ((int) Toolkit.getDefaultToolkit().getScreenSize().getHeight());
        getSortedTags(new File(url));

        mainTable = new JTable(mainTableEntries.toArray(new String[1][1]), columnNames);
        mainTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        mainTable.setShowGrid(false);
        mainTable.addMouseListener(new Listener());
        
        setSorter();
        setColumnSize(xSize);
        
        mainScroll = new JScrollPane(mainTable);
        mainScroll.setWheelScrollingEnabled(true);
        mainScroll.setPreferredSize(new Dimension((int)(0.65*xSize), (int)(0.65*ySize)));

        add(mainScroll, BorderLayout.SOUTH);
    }

    class Listener implements MouseListener
    {

        @Override
        public void mouseClicked(MouseEvent me)
        {
            if(me.getClickCount() == 1 && me.getButton() == MouseEvent.BUTTON1)
            {
                int row = mainTable.convertRowIndexToModel(mainTable.getSelectedRow());
                File song = songs.get(mainTableEntries.get(row));
    
                if(decoder != null && decoder.isPlaying())
                {
                    try
                    {
                        decoder.stop_playback();
                    } catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                }
                
                try
                {
                    decoder = new MusicDecoder("file:/"+song.toString());
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
    
                decoder.setMaxVolume();
                decoder.play_playback();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    private void setSorter()
    {
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(mainTable.getModel());
        sorter.setComparator(ARTIST, new RowComparator());
        sorter.setComparator(TITLE, new RowComparator());
        mainTable.setRowSorter(sorter);
    }
    
    private void setColumnSize(int width)
    {
        int wider = 2;
        TableColumn column;
        for (int i = 0; i < mainTable.getColumnCount(); i++) 
        {
            column = mainTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(width*(i<wider ? 2 : 1));
        }
    }
    
    private ArrayList<Tag> getSortedTags(File dir)
    {
        ArrayList<File> list = new ArrayList<File>();
        list.addAll(Arrays.asList(dir.listFiles()));   
        
        File potentialSong;
        AudioFile f;
        Tag tag;
        String datum[] = new String[size];
        while(!list.isEmpty())
        {
            potentialSong = list.remove(0);
            if(potentialSong.isDirectory())
            {
                list.addAll(Arrays.asList(potentialSong.listFiles()));
            }
            if(potentialSong.getName().endsWith(".mp3"))
            {
                // XXX exceptions?
                try
                {
                    f = AudioFileIO.read(potentialSong);
                    tag = f.getTag();
                    datum = new String[size];
                    
                    datum[ARTIST] = tag.getFirst(FieldKey.ARTIST);
                    datum[TITLE] = tag.getFirst(FieldKey.TITLE);
                    datum[RATING] = tag.getFirst(FieldKey.RATING);
                    datum[PLAYED] = "0"; // add play counter
                    datum[GENRE] = tag.getFirst(FieldKey.GENRE);
                    datum[BPM] = tag.getFirst(FieldKey.BPM);
                    
                    songs.put(datum, potentialSong);
                    
                    tags.add(tag);
                    mainTableEntries.add(datum);
                } catch (CannotReadException e)
                {
                    e.printStackTrace();
                } catch (IOException e)
                {
                    e.printStackTrace();
                } catch (TagException e)
                {
                    e.printStackTrace();
                } catch (ReadOnlyFileException e)
                {
                    e.printStackTrace();
                } catch (InvalidAudioFrameException e)
                {
                    e.printStackTrace();
                } catch (NullPointerException e)
                {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(tags, new TagComparator());
        
        return tags;
    }
    
    class ExtensionFilter implements FilenameFilter 
    {
        private String extension;

        public ExtensionFilter( String extension ) 
        {
          this.extension = extension;             
        }
        public boolean accept(File dir, String name) 
        {
          return (name.endsWith(extension));
        }
    }
    
    public static void main(String[] args)
    {
        new MusicPanel(args[0]);
    }

}
