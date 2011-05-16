package jarvis.music;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.io.IURLProtocolHandler;

public class MusicDecoder extends Thread
{
    private static SourceDataLine mLine;
    private IContainer container;
    private IPacket packet;
    private int audioStreamId;
    private IStreamCoder audioCoder;
    private String filename;
    private int offset = 0;
    
    private FloatControl volume; 
    private boolean pause = true;
    private boolean stop = false;
    private Object lock = new Object();
    private Thread thread = this;

    public MusicDecoder(String filename) throws InterruptedException
    {
        this.filename = filename;
        container = IContainer.make();

        if (container.open(filename, IContainer.Type.READ, null) < 0)
        {
            throw new IllegalArgumentException("could not open file: " + filename);
        }

        int numStreams = container.getNumStreams();
        
        audioStreamId = -1;
        for (int i = 0; i < numStreams; i++)
        {
            IStream stream = container.getStream(i);
            IStreamCoder coder = stream.getStreamCoder();

            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO)
            {
                audioStreamId = i;
                audioCoder = coder;
                break;
            }
        }
        if (audioStreamId == -1)
            throw new RuntimeException("could not find audio stream in container: " + filename);

        if (audioCoder.open() < 0)
            throw new RuntimeException("could not open audio decoder for container: " + filename);

        openJavaSound(audioCoder);

        packet = IPacket.make();
        
        this.start();
    }
    
    public void run()
    {
        while (container.readNextPacket(packet) >= 0 && !stop)
        {
            if (packet.getStreamIndex() == audioStreamId)
            {
                IAudioSamples samples = IAudioSamples.make(1024, audioCoder.getChannels());

                offset = 0;
                while (offset < packet.getSize())
                {
                    int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
                    if (bytesDecoded < 0)
                    {
                        quit();
                        throw new RuntimeException("got error decoding audio in: " + filename);
                    }
                    offset += bytesDecoded;
                    if (samples.isComplete())
                    {
                        playJavaSound(samples);
                    }
                    
                }
            }
            synchronized(lock)
            {
                if(pause)
                {
                    try
                    {
                        lock.wait();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        quit();
                    }
                }
                if(stop)
                {
                    break;
                }
            }
        }
        quit();
    }
    
    public void quit()
    {
        closeJavaSound();
        audioCoder.close();
        container.close();
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        if (args.length <= 0)
        {
            throw new IllegalArgumentException("must pass in a filename as the first argument");
        }

        new MusicDecoder(args[0]);
    }

    private void openJavaSound(IStreamCoder aAudioCoder)
    {
        AudioFormat audioFormat = new AudioFormat(aAudioCoder.getSampleRate(), 
                (int) IAudioSamples.findSampleBitDepth(aAudioCoder.getSampleFormat()), 
                aAudioCoder.getChannels(), true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try
        {
            mLine = (SourceDataLine) AudioSystem.getLine(info);
            mLine.open(audioFormat);

            volume = (FloatControl) mLine.getControl(FloatControl.Type.VOLUME);
            volume.setValue(0F);
            
            mLine.start();
        } catch (LineUnavailableException e)
        {
            throw new RuntimeException("could not open audio line");
        }
    }

    private static void closeJavaSound()
    {
        if (mLine != null)
        {
            mLine.close();
        }
    }

    private static void playJavaSound(IAudioSamples aSamples)
    {
        byte[] rawBytes = aSamples.getData().getByteArray(0, aSamples.getSize());
        
        mLine.write(rawBytes, 0, aSamples.getSize());
    }
    
    public void mute()
    {
        setVolume(0F);
    }
    
    public float getMaxVolume()
    {
        return volume.getMaximum();
    }
    
    public void setMaxVolume()
    {
        volume.setValue(volume.getMaximum());
    }
    
    public void setVolume(Float value)
    {
        volume.setValue(value);
    }
    
    public void play_playback()
    {
        synchronized(lock)
        {
            pause = false;
            lock.notify();
        }
    }
    
    public void pause_playback() throws InterruptedException
    {
        synchronized(lock)
        {
            pause = true;
        }
        
        while(thread.getState() != Thread.State.WAITING)
        {
            Thread.sleep(50);
        }
    }
    
    public void stop_playback() throws InterruptedException
    {
        synchronized(lock)
        {
            stop = true;
        }
    }
    
    // XXX header missing
    public void go_to_playback(long time) throws InterruptedException
    {
        pause_playback();

        container.seekKeyFrame(audioStreamId, 0, IURLProtocolHandler.SEEK_SET);        
        
        play_playback();
    }

    // XXX ?
    public void seek_playback(long time) throws InterruptedException
    {
        pause_playback();
        
        container.seekKeyFrame(audioStreamId, 2, IURLProtocolHandler.SEEK_CUR);
        
        play_playback();
    }
    
    public boolean isPlaying()
    {
        return !stop && !pause;
    }
    
    public boolean isPaused()
    {
        return !pause;
    }
}
