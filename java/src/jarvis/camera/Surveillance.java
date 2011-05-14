package jarvis.camera;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;
import april.jcam.ImageConvert;
import april.jcam.ImageSource;
import april.jcam.ImageSourceFormat;
import april.util.GetOpt;
import april.util.ParameterGUI;
import april.util.ParameterListener;
import april.vis.VisCanvas;
import april.vis.VisImage;
import april.vis.VisTexture;
import april.vis.VisWorld;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import edu.umich.mihai.camera.ImageReader;
import edu.umich.mihai.lcmtypes.image_path_t;


public class Surveillance implements LCMSubscriber, ImageReader.Listener, ParameterListener
{
/**
 * This is an example on how to use both ImageReader and the lcm message for image_path
 *      ImageReader is used for getting images directly (straight from the camera)
 *      LCM is used to get images indirectly from a log (from the HDD)
 */
    static LCM lcm = LCM.getSingleton();

    private ImageReader ir;
    private Object lock = new Object();
    private boolean bufferReady = false;
    private byte[] imageBuffer;
    private int width;
    private int height;
    private double timeStamp;
    private String format;
    
    private boolean run = true;
    
    private ParameterGUI pg;
    
    private JFrame jf;
    private VisWorld vw = new VisWorld();
    private VisCanvas vc = new VisCanvas(vw);
    private VisWorld.Buffer vbImage = vw.getBuffer("images");
    
    public Surveillance(String url) throws Exception
    {
        showGUI();
        
        ir = new ImageReader(true, false, 15, url);
        ir.addListener(this);
        ir.start();
        
        this.run();
    }
    
    public void showGUI()
    {
        pg.addDoubleSlider("pt", "pixel threshold", 0, 100, 0.1);
        pg.addIntSlider("vt", "variance threshold", 0, 128, 25);
        pg.addIntSlider("h", "temporalness", 0, 30, 5);
        pg.addIntSlider("sbp", "seconds btwn peeks", 0, 15, 1);
        
        pg.addListener(this);
        
        jf = new JFrame("Surveillance");
        jf.setLayout(new BorderLayout());
        jf.add(vc, BorderLayout.CENTER);
        jf.setSize(1000, 500);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
    }
    
    private String getDate() 
    {
        DateFormat dateFormat = new SimpleDateFormat("yy/MM/dd-HH:mm");
        Date date = new Date();
        
        return dateFormat.format(date);
    }
    
    public void run()
    {
        long time = 0;
        IMediaWriter writer = null;
        FilmStatus status = null;
        BufferedImage image;
        
        while(run)
        {
            synchronized(lock)
            {
                while(!bufferReady)
                {
                    try
                    {
                        lock.wait();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                
                if(status == null) 
                {
                    new FilmStatus(pg.gd("pt"), pg.gd("vt"), pg.gi("h"), width, height);
                }
                
                if(System.currentTimeMillis() - time > pg.gi("sbp")*1000)
                {
                    status.addImage(imageBuffer);
                    time = System.currentTimeMillis();
                }

                image = ImageConvert.convertToImage(format, width, height, imageBuffer);
            }
            
            vbImage.addBuffered(new VisImage(new VisTexture(image),new double[] { 0., 0, }, 
                    new double[] {image.getWidth(), image.getHeight() }, true));
            vbImage.switchBuffer();
            
            if( writer != null && writer.isOpen() )
            {
                writer.encodeVideo(0,image, (long)(timeStamp*1000000000), TimeUnit.NANOSECONDS);
                
                if(status.shouldStopFilming())
                {
                    writer.close();
                    writer = null;
                }
            }
            else if(status.shouldFilm())
            {
                writer = ToolFactory.makeWriter(getDate()+".mp4");
                writer.addVideoStream(0, 0, width, height);
            }
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        GetOpt opts = new GetOpt();
        opts.addBoolean('h', "help", false, "see this help screen");
        opts.addString('c', "camera", "dc", "camera to use for input");
        
        if (!opts.parse(args))
        {
            System.out.println("option error: " + opts.getReason());
        }
        
        if (opts.getBoolean("help"))
        {
            System.out.println("Usage: displays images from camera specified");  
            System.out.println("Cameras available:");
            ArrayList<String> urls = ImageSource.getCameraURLs();
            for(String url : urls)
            {
                System.out.println(url);
            }

            opts.doHelp();
            System.exit(1);
        }
        
        new Surveillance(opts.getString("camera"));
    }

    public void kill()
    {
        run = false;
    }
    
    @Override
    public void handleImage(byte[] im, ImageSourceFormat ifmt, double time, int camera)
    {
        synchronized(lock)
        {
            imageBuffer = im;
            width = ifmt.width;
            height = ifmt.height;
            format = ifmt.format;
            timeStamp = time;
            bufferReady = true;
            lock.notify();
        }
    }

    @Override
    public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins)
    {
        try
        {
            if (channel.contains("cam"))
            {
                synchronized(lock)
                {
                    image_path_t path = new image_path_t(ins);
                    if(imageBuffer == null) 
                    {
                        imageBuffer = new byte[path.height*path.width];
                    }
                    
                    new FileInputStream(new File(path.img_path)).read(imageBuffer);
                    width = path.width;
                    height = path.height;
                    format = path.format;
                    timeStamp = path.utime;
                    bufferReady = true;
                    lock.notify();
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void parameterChanged(ParameterGUI pg, String name)
    {
        
    }
}
