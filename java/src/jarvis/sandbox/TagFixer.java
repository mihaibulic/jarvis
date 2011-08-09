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
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class TagFixer
{
    private ArrayList<Tag> tags = new ArrayList<Tag>();

    class Song
    {
        String artist;
        String title;
        
        public Song(String artist, String title)
        {
            this.artist = artist;
            this.title = title;
        }
        
        @Override
        public boolean equals(Object a)
        {
            return ((Song)a).title.equals(title) &&
                   ((Song)a).artist.equals(artist);
        }
    }
    
    public TagFixer(String url)
    {
        File dir = new File(url);
        
        ArrayList<File> list = new ArrayList<File>();
        list.addAll(Arrays.asList(dir.listFiles()));   
        
        ArrayList<Song> songs = new ArrayList<Song>();
        
        HashMap<String,String> genres = new HashMap<String,String>();
        HashMap<String,String> artists= new HashMap<String,String>();
        
        try
        {
            BufferedReader inArtists = new BufferedReader(new FileReader("artists"));
            String str = "";
            while ((str = inArtists.readLine()) != null) 
            {
                StringTokenizer st2 = new StringTokenizer(str,"@");
                artists.put(st2.nextToken(), st2.nextToken());
            }

            BufferedReader inGenres = new BufferedReader(new FileReader("genres"));
            while ((str = inGenres.readLine()) != null)
            {
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
                    
                    String artist = fix(tag.getFirst(FieldKey.ARTIST));
                    String title = fix(tag.getFirst(FieldKey.TITLE));
                    
                    System.out.println("@@@ (" + tag.getFirst(FieldKey.ARTIST) + ") -> (" + artist + ")");
                    System.out.println("@@@ \t(" + tag.getFirst(FieldKey.TITLE) + ") -> (" + title + ")");
                    
                    if(artists.get(artist) != null)
                    {
                        artist = artists.get(artist);
                    }

                    // set genre
                    if(genres.get(artist) != null)
                    {
                        tag.setField(FieldKey.GENRE, genres.get(artist));
                    }
                    
                    tag.setField(FieldKey.TITLE, title);
                    tag.setField(FieldKey.ARTIST, artist);
                    tag.setField(FieldKey.ALBUM_ARTIST, artist);
                    tag.setField(FieldKey.ALBUM, "none"); // remove album name 
                    
                    // search for duplicates
                    Song test = new Song(artist, title);
                    if(songs.contains(test))
                    {
                        System.out.println("@@@@@ found copy:");
                        System.out.println("\t" + test.artist + " - " + test.title);
                        potentialSong.delete();
                    }
                    else
                    {
                        songs.add(test);
                        f.commit();
                    }
                    
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
    
    private String fix(String in)
    {
        if(in.isEmpty())
        {
            in = "unknown";
        }
        
        if(in.contains("(") && in.contains(")"))
        {
            String temp = in.substring(in.indexOf("("), in.indexOf(")"));
            if(!temp.contains("remix") && !temp.contains("Remix"))
            {
                in = in.replaceAll("\\(.*\\)", "");
            }
        }
        
        String[] keyWords = {"featuring", "ft.", "feat."};
        
        for(String word : keyWords)
        {
            if(in.contains(word))
            {
                in = in.substring(0, in.indexOf(word)-1);
            }
        }

        in = in.trim();
        
        boolean foundSpace = false;
        char[] a = in.toCharArray();
        in = in.substring(0, 1).toUpperCase();
        for(int x = 1; x < a.length; x++)
        {
            if(foundSpace)
            {
                in += Character.toUpperCase(a[x]);
                foundSpace = false;
            }
            else if(a[x] == ' ' || a[x] == '_')
            {
                in += " ";
                foundSpace = true;
            }
            else
            {
                in += a[x];
            }
        }
        
        return in.trim();
    }
    
    public static void main(String[] args)
    {
        new TagFixer(args[0]);
    }

}
