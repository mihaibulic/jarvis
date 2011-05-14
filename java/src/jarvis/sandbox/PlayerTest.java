package jarvis.sandbox;

import jarvis.music.MusicDecoder;

public class PlayerTest
{
    public PlayerTest(String file) throws InterruptedException
    {
        MusicDecoder mp3 = new MusicDecoder("file://home/april/jarvis/java/test2.mp3");
        mp3.mute();
        System.out.println("playing...");
        mp3.play_playback();

        float max = mp3.getMaxVolume();
        for(float vol = 0; vol <= max; vol+=max/500)
        {
            Thread.sleep(120);
            mp3.setVolume(vol);
        }
        
        Thread.sleep(5000);
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        new PlayerTest(args[0]);
    }
}
