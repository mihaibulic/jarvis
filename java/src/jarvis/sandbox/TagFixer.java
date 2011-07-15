package jarvis.sandbox;

import jarvis.music.TagComparator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class TagFixer
{
    private ArrayList<Tag> tags = new ArrayList<Tag>();

    public TagFixer(String url)
    {
        File dir = new File(url);
        
        ArrayList<File> list = new ArrayList<File>();
        list.addAll(Arrays.asList(dir.listFiles()));   
        
        HashMap<String,String> genres = new HashMap<String,String>();
        HashMap<String,String> artists= new HashMap<String,String>();
        
        try
        {
            BufferedReader inArtists = new BufferedReader(new FileReader("artists"));
            String str = "";
            while ((str = inArtists.readLine()) != null) 
            {
                System.out.println(str);
                StringTokenizer st2 = new StringTokenizer(str,"@");
                artists.put(st2.nextToken(), st2.nextToken());
            }

            BufferedReader inGenres = new BufferedReader(new FileReader("genres"));
            while ((str = inGenres.readLine()) != null)
            {
                System.out.println(str);
                StringTokenizer st2 = new StringTokenizer(str,"@");
                genres.put(st2.nextToken(), st2.nextToken());
            }
        } catch (IOException e1)
        {
            e1.printStackTrace();
        }
        
        File potentialSong;
        AudioFile f;
        Tag tag;
        while(!list.isEmpty())
        {
            potentialSong = list.remove(0);
            if(potentialSong.isDirectory())
            {
                list.addAll(Arrays.asList(potentialSong.listFiles()));
            }
            if(potentialSong.getName().endsWith(".mp3"))
            {
                try
                {
                    f = AudioFileIO.read(potentialSong);
                    tag = f.getTag();
                    
                    String artist = tag.getFirst(FieldKey.ARTIST);

                    if(genres.get(artist) != null)
                    {
                        tag.setField(FieldKey.GENRE, genres.get(artist));
                    }
                    
                    if(artists.get(artist) != null)
                    {
                        tag.setField(FieldKey.ARTIST, artists.get(artist));
                    }
                    
                    tag.setField(FieldKey.ALBUM, "none"); 
                    tag.setField(FieldKey.ALBUM_ARTIST, tag.getFirst(FieldKey.ARTIST));
                    
                    f.commit();
                } catch (CannotWriteException e)
                {
                    e.printStackTrace();
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
        
//        Collections.sort(artists);
//        for(String artist : artists)
//        {
//            System.out.println(artist);
//        }
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
        new TagFixer(args[0]);
    }

}