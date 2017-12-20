                /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.video;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import geometry.CenteredCircle;
import geometry.Vector2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import javax.imageio.ImageIO;
import mandelbrot.config.ZoomedFrame;
import mandelbrot.config.color.ColorPalette;
import static mandelbrot.controller.Controller.DEFAULT_MAX_ITER;
import static mandelbrot.controller.Controller.DEFAULT_ZOOM;
import mandelbrot.rendering.MdbRenderer;
import mandelbrot.rendering.iterationcalculator.IterationCalculatorSimple;
import mandelbrot.rendering.iterationcalculator.IterationCalculatorSimplePower;
import mandelbrot.rendering.parameterscalculator.ParameterCalculatorSimpleLog;
import mandelbrot.rendering.threading.MdbRendererThreaded;
import util.curves.SplineCubicLooped;

/**
 *
 * @author Gregoire
 */
public class BufferedVideoWriter {

    protected ArrayDeque<FrameJob> queue;
    protected Thread t;
    protected File file;
    protected IRational frame_rate;
    protected final IMediaWriter writer;
    protected WriteVideo wvRun;
    protected boolean stop;
    protected final int HEIGHT, WIDTH;

    public BufferedVideoWriter(File f, IRational frame_rate, int w, int h) {

        HEIGHT = h;
        WIDTH = w;

        queue = new ArrayDeque<>();

        this.file = f;
        this.frame_rate = frame_rate;
        writer = ToolFactory.makeWriter(file.getAbsolutePath());
        writer.addVideoStream(0, 0,frame_rate, WIDTH, HEIGHT);

        stop = false;
        wvRun = new WriteVideo(this);

        t = new Thread(wvRun);
        t.start();

    }

    public ArrayDeque<FrameJob> getQueue() {
        return queue;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public File getFile() {
        return file;
    }

    public IRational getFrame_rate() {
        return frame_rate;
    }

    public void setFrame_rate(IRational frame_rate) {
        this.frame_rate = frame_rate;
    }

    public Thread getT() {
        return t;
    }

    public void setT(Thread t) {
        this.t = t;
    }

    public WriteVideo getWvRun() {
        return wvRun;
    }

    public void setWvRun(WriteVideo wvRun) {
        this.wvRun = wvRun;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public IMediaWriter getWriter() {
        return writer;
    }

    public void endThread() {
        this.stop = true;
    }

    public void close() throws InterruptedException {

        Thread.sleep(100);
        stop = true;
        while (!wvRun.isJobDone()) {
            Thread.sleep(10);
        }

        writer.close();
    }

    public void addJob(FrameJob fj) {
        this.queue.push(fj);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ColorPalette palette = new ColorPalette(
                new SplineCubicLooped(new double[]{0.0, 0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.9375}, new double[]{0.2578125, 0.09765625, 0.03515625, 0.015625, 0.0, 0.046875, 0.09375, 0.22265625, 0.5234375, 0.82421875, 0.94140625, 0.96875, 0.99609375, 0.796875, 0.4140625},0,1), 0, 100,
                new SplineCubicLooped(new double[]{0.0, 0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.9375}, new double[]{0.1171875, 0.02734375, 0.00390625, 0.015625, 0.02734375, 0.171875, 0.3203125, 0.48828125, 0.70703125, 0.921875, 0.91015625, 0.78515625, 0.6640625, 0.5, 0.203125},0,1), 0, 100,
                new SplineCubicLooped(new double[]{0.0, 0.0625, 0.125, 0.1875, 0.25, 0.3125, 0.375, 0.4375, 0.5, 0.5625, 0.625, 0.6875, 0.75, 0.8125, 0.9375}, new double[]{0.05859375, 0.1015625, 0.18359375, 0.28515625, 0.390625, 0.5390625, 0.69140625, 0.81640625, 0.89453125, 0.96875, 0.74609375, 0.37109375, 0.0, 0.0, 0.01171875},0,1), 0, 100,
                false);
        MdbRendererThreaded exploRenderer
                = new MdbRendererThreaded(
                        new ZoomedFrame(MdbRenderer.DEFAULT_INITIAL_FRAME,
                                MdbRenderer.DEFAULT_INITIAL_FRAME.getCenter(), DEFAULT_ZOOM),
                        1920, 1080,
                        new IterationCalculatorSimplePower(new CenteredCircle(2), DEFAULT_MAX_ITER,2),
                        new ParameterCalculatorSimpleLog(),
                        palette);
        BufferedVideoWriter writer = new BufferedVideoWriter(new File("D:\\Test"+Math.random()+"fUltimateMandel.mp4"), IRational.make(24, 1), 1920, 1080);
        SplineCubicLooped l = (SplineCubicLooped) palette.getComp1Function();
        double x,y  ;
        exploRenderer.getCurrentFrame().getCenter().setCoord(x=-1.7496999138086122, y=3.466984598572184E-8);
        
        exploRenderer.getIterationCalculator().setMaxIter(10000);
        
        int zoomI =0;double zoom=1;
        while(zoom<9.256148959232E12){
            //exploRenderer.getIterationCalculator().setMaxIter(iter);
             exploRenderer.getCurrentFrame().setZoom(zoom);
             exploRenderer.computeImage();
           
            exploRenderer.getPool().waitEndOfWork();
            writer.addJob(new FrameJob(1, exploRenderer.getMdbImage()));
            
            zoom = Math.pow(10,++zoomI/40.0 );
            System.out.println("Zooom = "+zoom+" and ZoomI = "+zoomI);
        }
        writer.close();
       
//        while(power<30){
//            ((IterationCalculatorSimplePower)exploRenderer.getIterationCalculator()).setPower(power);
//            exploRenderer.computeImage();
//            exploRenderer.getPool().waitEndOfWork();
//            writer.addJob(new FrameJob(5, exploRenderer.getMdbImage()));
//            power++;
//            System.out.println(""+power);
//        }
//        exploRenderer.getCurrentFrame().setZoom(2.7532514058003945E12);
//        
//        int iter =100;
//        System.out.println(writer.file.getPath());
//        while(iter<1000){
//            exploRenderer.getIterationCalculator().setMaxIter(iter);
//            exploRenderer.computeImage();
//            exploRenderer.getPool().waitEndOfWork();
//            writer.addJob(new FrameJob(1, exploRenderer.getMdbImage()));
//            System.out.println(""+(iter-100.0)/(1000.0-100)*100+"%");
//            iter+=step;
//        }
//        
//        while(zoom>1){
//            
//            exploRenderer.getCurrentFrame().setZoom(zoom);
//            exploRenderer.computeImage();
//            exploRenderer.getPool().waitEndOfWork();
//            writer.addJob(new FrameJob(1, exploRenderer.getMdbImage()));
//            zoom = Math.pow(10, zoomI/40.0);
//            System.out.println(zoom/2.7532514058003945E12*100+"%");
//            zoomI-=step;
//        }

    }

}
