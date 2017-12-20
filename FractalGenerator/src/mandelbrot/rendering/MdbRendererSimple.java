package mandelbrot.rendering;

// @author Gregoire & Raphael

import geometry.Vector2D;
import java.awt.image.BufferedImage;
import mandelbrot.config.ZoomedFrame;
import mandelbrot.config.color.ColorPalette;
import mandelbrot.rendering.iterationcalculator.IterationCalculator;
import mandelbrot.rendering.iterationcalculator.IterationResult;
import mandelbrot.rendering.parameterscalculator.ParameterResult;
import mandelbrot.rendering.parameterscalculator.ParametersCalculator;


public class MdbRendererSimple extends MdbRenderer
{  

    public MdbRendererSimple(ZoomedFrame currentFrame, int width, int height, IterationCalculator iterationCalculator, ParametersCalculator parametersCalculator, ColorPalette palette) {
        super(currentFrame, width, height, iterationCalculator, parametersCalculator, palette);
    }
    
    
    @Override
    public void computeImage()
    {
        Vector2D upLeftCorner = getEffectiveUpperLeftCorner();
        double resolution = getResolution();
        
        mdbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int Y = 0; Y < height; Y++)
        {
            for(int X = 0; X < width; X++)
            {
                IterationResult res= iterationCalculator.computeIteration(upLeftCorner.getX()+X/resolution,upLeftCorner.getY()-Y/resolution);
                ParameterResult parameterResult = parametersCalculator.computeParameters(res);
                
                mdbImage.setRGB(X, Y, palette.getColorFromParam(parameterResult));
            }
        }
        
        sendCanRepaintSignal();
    }
    
    @Override
    protected void computeImage(int X, int Y, double zoomMultiplier, boolean zoomIn)
    {
        computeImage();
    }
    
    @Override
    public void declarePixelInMandelbrotSet(int X, int Y)
    {
    }

    @Override
    public void renderImage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
