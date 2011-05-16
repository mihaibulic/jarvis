package jarvis.music;

import java.util.Comparator;

public class RowComparator implements Comparator<Object>
{
    private String prefixes[] = {"The ", "Dj "};
    
    /**
     * Compares two Tags and returns 1 if tag1 precedes tag2 alphabetically 
     * 
     * @author Mihai Bulic
     *
     */
    public int compare(Object row1, Object row2)
    {
        String a = (String) row1;
        String b = (String) row2;
        
        for(String prefix : prefixes)
        {
            if(a.startsWith(prefix))
            {
                a = a.substring(prefix.length());
            }
            if(b.startsWith(prefix))
            {
                b = b.substring(prefix.length());
            }
        }
        
        return a.compareTo(b);
    }
}
