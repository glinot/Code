package mandelbrot.rendering;

// @author Gregoire

import java.awt.image.BufferedImage;
import geometry.StraightRectangle;
import geometry.Vector2D;
import java.util.ArrayList;
import mandelbrot.config.ZoomedFrame;
import mandelbrot.config.color.ColorPalette;
import mandelbrot.rendering.iterationcalculator.IterationCalculator;
import mandelbrot.rendering.parameterscalculator.ParameterResult;
import mandelbrot.rendering.parameterscalculator.ParametersCalculator;


public abstract class MdbRenderer
{
    public static final StraightRectangle DEFAULT_INITIAL_FRAME = new StraightRectangle(-2, -1.2, 0.7, 1.2);
    
    protected ZoomedFrame currentFrame;
    protected int width, height;
    protected volatile BufferedImage mdbImage;
    protected ArrayList<MdbRendererListener> listeners = new ArrayList<>();
    
    protected ParameterResult [][] parametersMatrix;
    protected IterationCalculator iterationCalculator;
    protected ParametersCalculator parametersCalculator;
    protected ColorPalette palette;

    public MdbRenderer(ZoomedFrame currentFrame, int width, int height, IterationCalculator iterationCalculator, ParametersCalculator parametersCalculator, ColorPalette palette) {
        this.currentFrame = currentFrame;
        this.width = width;
        this.height = height;
        this.iterationCalculator = iterationCalculator;
        this.parametersCalculator = parametersCalculator;
        this.palette = palette;
    } 
    
    public void computeZoomedImage(int XOnImage, int YOnImage, double zoomMultiplier, boolean zoomIn)
    {
        double resolution = getResolution();
        currentFrame.getCenter().add((XOnImage-width/2)/resolution,
                                        -(YOnImage-height/2)/resolution);
        
        if(zoomIn)
        {
            currentFrame.zoomIn(zoomMultiplier);
        }
        else
        {
            currentFrame.zoomOut(zoomMultiplier);
        }
        
        computeImage(XOnImage, YOnImage, zoomMultiplier, zoomIn);
    }
    
    public abstract void renderImage();
    public abstract void computeImage();
    protected abstract void computeImage(int X, int Y, double zoomMultiplier, boolean zoomIn);
    
    public Vector2D getEffectiveUpperLeftCorner()
    {
        double resolution = getResolution(),
               Dx = width/resolution,
               Dy = height/resolution,
               x0 = currentFrame.getCenter().getX()-Dx/2, // coordinates of the upper-left corner of the current frame,
               y0 = currentFrame.getCenter().getY()+Dy/2; // recalculated with the actual image proportions
        return new Vector2D(x0, y0);
    }
    
    // resolution in pixel per plan unit
    public double getResolution()
    {
        if((width+0.0)/height >= currentFrame.getFormat())
        {
            return height/currentFrame.getHeight();
        }
        else
        {
            return width/currentFrame.getWidth();
        }
    }
    
    public abstract void declarePixelInMandelbrotSet(int X, int Y);
    
    public ZoomedFrame getCurrentFrame()
    {
        return currentFrame;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    
    
    public BufferedImage getMdbImage()
    {
        return mdbImage;
    }
    
    public ArrayList<MdbRendererListener> getListeners()
    {
        return listeners;
    }
    
    public void setCurrentFrame(ZoomedFrame currentFrame)
    {
        this.currentFrame = currentFrame;
    }
    
    public void setWidth(int width)
    {
        this.width = width;
    }
    
    public void setHeight(int height)
    {
        this.height = height;
    }
    
 
    
    public void setListeners(ArrayList<MdbRendererListener> listeners)
    {
        this.listeners = listeners;
    }
    
    public void addMdbRendererListener(MdbRendererListener l)
    {
        listeners.add(l);
    }
    
    public void removeMdbRendererListener(MdbRendererListener l)
    {
        listeners.remove(l);
    }
    
    public void sendCanRepaintSignal()
    {
        for(MdbRendererListener l : listeners)
            l.canRepaint();
    }

    public ParameterResult[][] getParametersMatrix() {
        return parametersMatrix;
    }

    public void setParametersMatrix(ParameterResult[][] parametersMatrix) {
        this.parametersMatrix = parametersMatrix;
    }

    public IterationCalculator getIterationCalculator() {
        return iterationCalculator;
    }

    public void setIterationCalculator(IterationCalculator iterationCalculator) {
        this.iterationCalculator = iterationCalculator;
    }

    public ParametersCalculator getParametersCalculator() {
        return parametersCalculator;
    }

    public void setParametersCalculator(ParametersCalculator parametersCalculator) {
        this.parametersCalculator = parametersCalculator;
    }

    public ColorPalette getPalette() {
        return palette;
    }

    public void setPalette(ColorPalette palette) {
        this.palette = palette;
    }

   
    
}
