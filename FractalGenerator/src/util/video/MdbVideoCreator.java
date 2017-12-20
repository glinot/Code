/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.video;

import com.xuggle.xuggler.IRational;
import geometry.CenteredCircle;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import mandelbrot.config.ZoomedFrame;
import mandelbrot.config.color.ColorPalette;
import static mandelbrot.controller.Controller.DEFAULT_MAX_ITER;
import static mandelbrot.controller.Controller.DEFAULT_ZOOM;
import mandelbrot.rendering.MdbRenderer;
import mandelbrot.rendering.iterationcalculator.IterationCalculatorSimple;
import mandelbrot.rendering.parameterscalculator.ParameterCalculatorSimpleLog;
import mandelbrot.rendering.threading.MdbRendererThreaded;
import util.curves.Function2D;
import util.curves.SplineCubicLooped;

/**
 *
 * @author Gregoire
 */
public class MdbVideoCreator implements Runnable{

    private BufferedVideoWriter writer;
    private MdbRendererThreaded renderer;
    private ZoomedFrame currentFrame;
    private Function2D iterationCurve;
    private Function2D zoomCurve;
    private int nbOfFrame;
    private final double totalTime;

    public MdbVideoCreator(File f, MdbRendererThreaded renderer, Function2D iterationCurve, Function2D zoomCurve, double timeOfVideo) {
        this.writer = new BufferedVideoWriter(f, IRational.make(24), renderer.getWidth(), renderer.getHeight());
        this.renderer = renderer;
        this.iterationCurve = iterationCurve;
        this.zoomCurve = zoomCurve;
        this.totalTime = timeOfVideo;
        this.currentFrame = renderer.getCurrentFrame();
        this.nbOfFrame =(int) (timeOfVideo*writer.frame_rate.getDouble());
        
    }

    
    public void renderVideo(){
        int iter =0;
        while(iter<nbOfFrame){
            double time = iter/writer.getFrame_rate().getDouble();
            double zoom =zoomCurve.eval(time);
            int iterMax = (int)iterationCurve.eval(time).doubleValue();
            currentFrame.setZoom(zoom/100);
            renderer.getIterationCalculator().setMaxIter(iterMax);
            renderer.computeImage();
            renderer.getPool().waitEndOfWork();
            writer.addJob(new FrameJob(1, (renderer.getMdbImage())));
            System.out.println(time/totalTime*100+"%");
            iter++;
            
        }
        try {
            writer.close();
        } catch (InterruptedException ex) {
            Logger.getLogger(MdbVideoCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public BufferedVideoWriter getWriter() {
        return writer;
    }

    public void setWriter(BufferedVideoWriter writer) {
        this.writer = writer;
    }

    public MdbRendererThreaded getRenderer() {
        return renderer;
    }

    public void setRenderer(MdbRendererThreaded renderer) {
        this.renderer = renderer;
    }

    public ZoomedFrame getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(ZoomedFrame currentFrame) {
        this.currentFrame = currentFrame;
    }

    public Function2D getIterationCurve() {
        return iterationCurve;
    }

    public void setIterationCurve(Function2D iterationCurve) {
        this.iterationCurve = iterationCurve;
    }

    public Function2D getZoomCurve() {
        return zoomCurve;
    }

    public void setZoomCurve(Function2D zoomCurve) {
        this.zoomCurve = zoomCurve;
    }

    public int getNbOfFrame() {
        return nbOfFrame;
    }

    public void setNbOfFrame(int nbOfFrame) {
        this.nbOfFrame = nbOfFrame;
    }

    @Override
    public void run() {
        renderVideo();
    }

    static class LinearFunction extends Function2D{
        private double a;
        private double b;

        public LinearFunction(double a, double b) {
            this.a = a;
            this.b = b;
        }
        
        
        @Override
        public Double eval(double x) {
            return a*x+b;
        }

       
        
    }
    static class ExponetialFunction extends Function2D{

        @Override
        public Double eval(double x) {return Math.exp(x/2);};
        
    }
    
    public static void main(String[] args) {
        ColorPalette palette = new ColorPalette(
                new SplineCubicLooped(new double[]{0.0, 0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.9375}, new double[]{0.2578125, 0.09765625, 0.03515625, 0.015625, 0.0, 0.046875, 0.09375, 0.22265625, 0.5234375, 0.82421875, 0.94140625, 0.96875, 0.99609375, 0.796875, 0.4140625}, 0, 1), 0, 100,
                new SplineCubicLooped(new double[]{0.0, 0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.9375}, new double[]{0.1171875, 0.02734375, 0.00390625, 0.015625, 0.02734375, 0.171875, 0.3203125, 0.48828125, 0.70703125, 0.921875, 0.91015625, 0.78515625, 0.6640625, 0.5, 0.203125}, 0, 1), 0, 100,
                new SplineCubicLooped(new double[]{0.0, 0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.9375}, new double[]{0.05859375, 0.1015625, 0.18359375, 0.28515625, 0.390625, 0.5390625, 0.69140625, 0.81640625, 0.89453125, 0.96875, 0.74609375, 0.37109375, 0.0, 0.0, 0.01171875}, 0, 1), 0, 100,
                false);
        MdbRendererThreaded exploRenderer
                = new MdbRendererThreaded(
                        new ZoomedFrame(MdbRenderer.DEFAULT_INITIAL_FRAME,
                                MdbRenderer.DEFAULT_INITIAL_FRAME.getCenter(), DEFAULT_ZOOM),
                        1920, 1080,
                        new IterationCalculatorSimple(new CenteredCircle(2), DEFAULT_MAX_ITER),
                        new ParameterCalculatorSimpleLog(),
                        palette);
        double xVal,yVal;
        exploRenderer.getCurrentFrame().getCenter().setCoord(xVal=0.30305078355690546, yVal=-0.021435633383953406);
        MdbVideoCreator vid = new MdbVideoCreator(new File("D:\\test514.mp4"),exploRenderer, new LinearFunction(0, 1000), new ExponetialFunction(),1);
        vid.renderVideo();
    }
    
    

}
