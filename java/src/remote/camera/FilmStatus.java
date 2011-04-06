package remote.camera;

public class FilmStatus
{
    private byte[][] oldImages;
    private int oldestImage = 0;
    private int[] variance;
    
    private double pixelThreshold;
    private double varianceThreshold;
    
    public FilmStatus(double pixelThreshold, double varianceThreshold, int history, int width, int height)
    {
        this.pixelThreshold = pixelThreshold;
        this.varianceThreshold = varianceThreshold;
        
        oldImages = new byte[width*height][history];
    }
    
    public void addImage(byte[] image)
    {
        oldImages[oldestImage] = image;
        
        calculateVariance();
    }

    private void calculateVariance()
    {
        for(int a = 0; a < variance.length; a++)
        {
            int average = 0;
            for(int b = 0; b < oldImages[a].length; a++)
            {
                average += oldImages[a][b];
            }
            average = variance[a] / oldImages[a].length;
            
            for(int b = 0; b < oldImages[a].length; a++)
            {
                variance[a] += (int)(Math.pow(average - oldImages[a][b], 2));
            }
            
            variance[a] /= oldImages[a].length;
        }
    }
    
    public boolean shouldFilm()
    {
        int count = 0;
        
        for(int a = 0; a < variance.length; a++)
        {
            if(variance[a] > varianceThreshold)
            {
                count++;
            }
        }
        
        return (count > variance.length*pixelThreshold);
    }
    
    public boolean shouldStopFilming()
    {
        return !shouldFilm();
    }
    
}
