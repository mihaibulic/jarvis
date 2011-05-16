package jarvis.sandbox;

import jarvis.music.MusicDecoder;

public class PlayerTest
{
    public PlayerTest() throws InterruptedException
    {
        MusicDecoder mp3 = new MusicDecoder("file://home/april/jarvis/test/Dub/The Devil Wears Prada - Still Fly.mp3");
        mp3.mute();
        System.out.println("playing...");
        mp3.play_playback();
        mp3.setMaxVolume();
//        float max = mp3.getMaxVolume();
//        for(float vol = 0; vol <= max; vol+=max/500)
//        {
//            Thread.sleep(120);
//            mp3.setVolume(vol);
//        }
        
        Thread.sleep(5000);
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        new PlayerTest();
    }
}
