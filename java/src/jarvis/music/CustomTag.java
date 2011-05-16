package jarvis.music;

import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

/**
 * 
 * @deprecated - Made this for the toString method, but it's not needed (for now)
 * @author Mihai Bulic
 *
 */
public class CustomTag
{
    public static final int ARTIST     = 0;
    public static final int TITLE      = 1;
    public static final int RATING     = 2;
    public static final int PLAYED     = 3;
    public static final int GENRE      = 4;
    public static final int BPM        = 5;
    private int size = 6;

    private int mode = 0;
    public static final int MODE_ARTIST_TITLE = 11;
    public static final int MODE_TITLE_ARTIST = 12;

    public Tag tag;
    
    private String[] info;
    private String fieldBreak = " - ";
    
    public CustomTag(Tag tag) throws NullPointerException
    {
        this.tag = tag;
        
        info = new String[size];
        info[ARTIST] = tag.getFirst(FieldKey.ARTIST);
        info[TITLE] = tag.getFirst(FieldKey.TITLE);
        info[RATING] = tag.getFirst(FieldKey.RATING);
        info[PLAYED] = "0"; // add play counter
        info[GENRE] = tag.getFirst(FieldKey.GENRE);
        info[BPM] = tag.getFirst(FieldKey.BPM);
    }
    
    public String[] get(int... fields)
    {
        String[] info = new String[fields.length];
        
        for(int x = 0; x < info.length; x++)
        {
            switch(fields[x])
            {
                case ARTIST:
                    info[x] = tag.getFirst(FieldKey.ARTIST);
                    break;
                case TITLE:
                    info[x] = tag.getFirst(FieldKey.ARTIST);            
                    break;
                case RATING:
                    info[x] = tag.getFirst(FieldKey.RATING);
                    break;
                case PLAYED:
                    info[x] = "0"; // XXX
                    break;
                case GENRE:
                    info[x] = tag.getFirst(FieldKey.GENRE);
                    break;
                case BPM:
                    info[x] = tag.getFirst(FieldKey.BPM);
                    break;
            }
        }
        
        return info;
    }
    
    public String[] get()
    {
        return info;
    }
    
    public String get(int type)
    {
        return info[type];
    }
    
    public void set(String setTo, int type)
    {
        info[type] = setTo;
    }
    
    public void setStringMode(int mode)
    {
        this.mode = mode;
    }
    
    public String toString()
    {
        String output = "";
        
        switch(mode)
        {
            case MODE_ARTIST_TITLE:
                output += info[ARTIST] + fieldBreak + info[TITLE];
                break;
            case MODE_TITLE_ARTIST:
                output += info[TITLE] + fieldBreak + info[ARTIST];            
                break;
            case TITLE:
                output += info[TITLE];
                break;
            case ARTIST:
                output += info[ARTIST];
                break;
        }
        
        return output;
    }
    
}
