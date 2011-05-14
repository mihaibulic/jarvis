package jarvis.music;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class MusicPlayer
{
    public MusicPlayer() throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException, InterruptedException
    {
        File dir = new File("/home/april/jarvis/test/");
        String[] files = dir.list(new Filter());

        ArrayList<Tag> tags = new ArrayList<Tag>();
        for(String song : files)
        {
            if(song.endsWith(".mp3"))
            {
                AudioFile f = AudioFileIO.read(new File(song));
                tags.add(f.getTag());
            }
        }
        
        System.out.println("\n\n\n\n\n");
        for(Tag t : tags)
        {
            System.out.println("A: " + t.getFirst(FieldKey.ARTIST) + "\tT: " + t.getFirst(FieldKey.TITLE));
        }
        
//        String song = "/home/april/jarvis/java/test2.mp3";
//        AudioFile f = AudioFileIO.read(new File(song));
//        Tag tag = f.getTag();
//        AudioHeader header = f.getAudioHeader();
//        
//        System.out.println("Track length\t" + header.getTrackLength()/60 + ":" + header.getTrackLength()%60);
//        System.out.println("Artist\t" + tag.getFirst(FieldKey.ARTIST));
//        System.out.println("Title\t" + tag.getFirst(FieldKey.TITLE));
//        
//        MusicDecoder md = new MusicDecoder(song);
//        md.setMaxVolume();
//        md.play_playback();
    }
    
    class Filter implements FilenameFilter
    {
        @Override
        public boolean accept(File file, String name)
        {
            return name.endsWith(".mp3");
        }
    }
    
    public static void main(String[] args) throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException, InterruptedException
    {
        new MusicPlayer();
    }

}
