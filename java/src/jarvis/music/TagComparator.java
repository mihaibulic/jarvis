package jarvis.music;

import java.util.Comparator;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class TagComparator implements Comparator<Tag>
{
    private String prefixes[] = {"The ", "Dj "};
    
    /**
     * Compares two Tags and returns 1 if tag1 precedes tag2 alphabetically 
     * 
     * @author Mihai Bulic
     *
     */
    public int compare(Tag tag1, Tag tag2)
    {
        String artist1 = tag1.getFirst(FieldKey.ARTIST);
        String artist2 = tag2.getFirst(FieldKey.ARTIST);
        
        for(String prefix : prefixes)
        {
            if(artist1.startsWith(prefix))
            {
                artist1 = artist1.substring(prefix.length());
            }
            if(artist2.startsWith(prefix))
            {
                artist2 = artist2.substring(prefix.length());
            }
        }
        
        int compare = artist1.compareTo(artist2);
        if(compare == 0)
        {
            String title1 = tag1.getFirst(FieldKey.TITLE);
            String title2 = tag2.getFirst(FieldKey.TITLE);
        
            for(String prefix : prefixes)
            {
                if(title1.startsWith(prefix))
                {
                    title1 = title1.substring(prefix.length());
                }
                if(title2.startsWith(prefix))
                {
                    title2 = title2.substring(prefix.length());
                }
                
                compare = title1.compareTo(title2);
            }
        }
        
        return compare;
    }
}
