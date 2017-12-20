package mandelbrot.rendering.threading;

// @author Gregoire
import geometry.Vector2D;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import mandelbrot.config.ZoomedFrame;
import mandelbrot.config.color.ColorPalette;
import mandelbrot.rendering.GenerationMatrix;
import mandelbrot.rendering.MdbRenderer;
import mandelbrot.rendering.MdbRendererListener;
import mandelbrot.rendering.iterationcalculator.IterationCalculator;
import mandelbrot.rendering.parameterscalculator.ParameterResult;
import mandelbrot.rendering.parameterscalculator.ParametersCalculator;

public class MdbRendererThreaded extends MdbRenderer {

    protected ThreadPool pool;
    protected int threadsNbr;
    protected int parcelsNbr = 10;
    protected Lock queueAccess = new ReentrantLock();
    protected Lock toGenerateAccess = new ReentrantLock();
    private boolean[][] toGenerate;
    private PreviewGenerationMatrix generationMatrix;
    private ImageRenderer imageRenderer;

    // --- Timer Section ---
    private long lastStart;
    private long timeGap = 200;

    public MdbRendererThreaded(ZoomedFrame currentFrame, int width, int height, IterationCalculator iterationCalculator, ParametersCalculator parametersCalculator, ColorPalette palette) {
        super(currentFrame, width, height, iterationCalculator, parametersCalculator, palette);
        listeners = new ArrayList<>();
        threadsNbr = 7;
        imageRenderer = new ImageRenderer(this);
        lastStart = System.currentTimeMillis();
    }

    @Override
    public void computeImage() {

        System.out.println(currentFrame.getCenter() + "  Zoom= " + currentFrame.getZoom());
        parametersMatrix = new ParameterResult[width][height];

        updateProgessBars(0);
        mdbImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        int step = 5;
        generationMatrix = new PreviewGenerationMatrix(Math.round((float) width / step), Math.round((float) height / step), step);
        ArrayList<Parcel> parcels = getParcels();
        if (pool == null) {

            pool = new ThreadPool(parcels, this);
        } else {
            pool.stopPool();
            pool.removeJobs();
        }

        pool.addJobs(parcels);
        pool.setStep(step);
        pool.start(ThreadPool.RenderingType.PREVIEW);
        pool.waitEndOfWork();
        pool.stopPool();

        generationMatrix.trim();
        generationMatrix.trim();
        generationMatrix.trim();

        //   generationMatrix=generationMatrix.getStretchedInstance(Math.round((float) width / step), Math.round((float) height / step),step);
//          pool.setStep(5);
//          pool.addJobs(parcels);
//          pool.start(ThreadPool.RenderingType.PREVIEW);
//          pool.waitEndOfWork();
////          generationMatrix.trim();
//        pool.stopPool();
        pool.addJobs(parcels);
        updateProgessBars(0);
        pool.setStep(1);
        pool.start(ThreadPool.RenderingType.RENDERING);

    }

    @Override
    public void renderImage() {
        if (System.currentTimeMillis() - lastStart > timeGap) {
            // reset timer  
            lastStart = System.currentTimeMillis();
            
            
            
            boolean recompute = false;

            if (parametersMatrix == null) {
                recompute = true;
            } else {
                if (parametersMatrix[0] != null) {
                    if (parametersMatrix.length != width || parametersMatrix[0].length != height) {
                        recompute = true;
                    }
                } else {
                    recompute = true;
                }

            }
            if (recompute) {
                updateProgessBars(0);
                computeImage();
                pool.waitEndOfWork();

            }
            updateProgessBars(0);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    ParameterResult res = parametersMatrix[i][j];
                    mdbImage.setRGB(i, j, palette.getColorFromParam(res));
                }
                updateProgessBars((double) i / width);
            }

//        imageRenderer.renderImage();
            sendCanRepaintSignal();
            timeGap = (long)((System.currentTimeMillis() -lastStart)*1.8);
        }

    }

    public ArrayList<Parcel> getParcelsSquared() {
        Vector2D upLeftCorner = getEffectiveUpperLeftCorner();
        double resolution = getResolution();

        ArrayList<Parcel> parcels = new ArrayList<>();
        int toCutY = height;

        int ySclice = (int) ((double) height / parcelsNbr);
        int xSclice = (int) ((double) width / parcelsNbr);
        int y = 0;

        while (toCutY > 0) {
            int dyToSet = toCutY - ySclice > 0 ? ySclice : toCutY;
            int x = 0;
            int toCutX = width;
            while (toCutX > 0) {
                int dxToSet = toCutX - xSclice > 0 ? xSclice : toCutX;
                Parcel p = new Parcel(xSclice * x, ySclice * y, dxToSet, dyToSet,
                        upLeftCorner.getX() - (xSclice * x) / resolution,
                        upLeftCorner.getY() - (ySclice * y) / resolution,
                        resolution);
                parcels.add(p);
                x++;
                toCutX -= xSclice;
            }

            y++;
            toCutY -= ySclice;

        }

        return parcels;
    }

    public ArrayList<Parcel> getParcels() {
        Vector2D upLeftCorner = getEffectiveUpperLeftCorner();
        double resolution = getResolution();

        ArrayList<Parcel> parcels = new ArrayList<>();
        int toCut = height;
        int ySclice = (int) ((double) height / parcelsNbr);
        int i = 0;

        while (toCut > 0) {
            int dyToSet = toCut - ySclice > 0 ? ySclice : toCut;
            Parcel p = new Parcel(0, ySclice * i, width, dyToSet,
                    upLeftCorner.getX(),
                    upLeftCorner.getY() - (ySclice * i) / resolution,
                    resolution);
            i++;
            toCut -= ySclice;
            parcels.add(p);
        }

        return parcels;
    }

    public int getThreadsNbr() {
        return threadsNbr;
    }

    public void setThreadsNbr(int theadsNbr) {
        this.threadsNbr = theadsNbr;
    }

    /*protected void updateNbOfThreads(int theadsNbr) {
     this.threadsNbr = theadsNbr;
     }*/
    public int getParcelsNbr() {
        return parcelsNbr;
    }

    public void setParcelsNbr(int parcelsNbr) {
        this.parcelsNbr = parcelsNbr;
    }

    public ThreadPool getPool() {
        return pool;
    }

    public void setPool(ThreadPool pool) {
        this.pool = pool;
    }

    public Lock getQueueAccess() {
        return queueAccess;
    }

    public void setQueueAccess(Lock queueAccess) {
        this.queueAccess = queueAccess;
    }

    public boolean noComputing() {
        return this.pool.isJobIsDone();
    }

    @Override
    protected void computeImage(int X, int Y, double zoomMultiplier, boolean zoomIn) {
        computeImage();
    }

    @Override
    public void declarePixelInMandelbrotSet(int X, int Y) {
        // generationMatrix.setValAt(X, Y, true);
    }

    public boolean[][] getToGenerate() {
        return toGenerate;
    }

    public void setToGenerate(boolean[][] toGenerate) {
        this.toGenerate = toGenerate;
    }

    public Lock getToGenerateAccess() {
        return toGenerateAccess;
    }

    public void setToGenerateAccess(Lock toGenerateAccess) {
        this.toGenerateAccess = toGenerateAccess;
    }

    public PreviewGenerationMatrix getGenerationMatrix() {
        return generationMatrix;
    }

    public void setGenerationMatrix(PreviewGenerationMatrix generationMatrix) {
        this.generationMatrix = generationMatrix;
    }

    public void updateProgessBars(double i) {
        for (MdbRendererListener listener : listeners) {
            listener.updateProgessBar(i);
        }
    }

}
