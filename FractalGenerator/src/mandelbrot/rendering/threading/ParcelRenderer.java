package mandelbrot.rendering.threading;

// @author Gregoire
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import mandelbrot.rendering.iterationcalculator.IterationResult;
import mandelbrot.rendering.parameterscalculator.ParameterResult;

public class ParcelRenderer implements Runnable {

    //protected MandelbrotRenderingConfig config;
    protected ArrayList<ParcelRendererListenner> listeners = new ArrayList();
    protected MdbRendererThreaded renderer;
    protected boolean stop;

    public ParcelRenderer(MdbRendererThreaded renderer) {

        this.renderer = renderer;
        //this.config=  renderer.getConfig();

    }

    public void updateParcelRendererListenners() {
        for (ParcelRendererListenner t : listeners) {
            t.parcelRendered();
        }
    }

    public void addParcelRendererListenner(ParcelRendererListenner l) {
        listeners.add(l);
    }

    public void removeParcelRendererListenner(ParcelRendererListenner l) {
        listeners.remove(l);
    }

    @Override
    public void run() {

        ArrayDeque<Parcel> jobQueue = jobQueue = renderer.getPool().getQueue();
        boolean go = true;
        Parcel p;

        while (go) {

            this.renderer.getQueueAccess().lock();

            try {
                if (jobQueue.isEmpty()) {
                    go = false;
                    continue;
                }

                p = jobQueue.pop();
            } finally {
                this.renderer.getQueueAccess().unlock();
            }

            double resolution = p.getResolution();

            int step = renderer.getPool().getStep();
            int maxY = renderer.getGenerationMatrix().getHeight() - 1;
            int maxX = renderer.getGenerationMatrix().getWidth() - 1;
            for (int X = 0; X < p.getDX(); X += step) {
                for (int Y = 0; Y < p.getDY(); Y += step) {

                    if (this.renderer.getPool().isStop()) {
                        stop = true;
                        return;

                    }

                    int x = Math.min((int) Math.round((double) (p.getX() + X) / renderer.getGenerationMatrix().getStep()), maxX);
                    int y = Math.min((int) Math.round((double) (p.getY() + Y) / renderer.getGenerationMatrix().getStep()), maxY);

                    if (!renderer.getGenerationMatrix().getValAt(x, y)) {
                        double realX = 0, realY = 0; //  x,y on Real plane
                        if (renderer.getPool().getTarget() == ThreadPool.RenderingType.RENDERING) {
                            realX = p.getX0() + X / resolution;
                            realY = p.getY0() - Y / resolution;
                        }
                        if (renderer.getPool().getTarget() == ThreadPool.RenderingType.PREVIEW) {
                            realX = p.getX0() + (X + renderer.getGenerationMatrix().getStep() / 2) / resolution;
                            realY = p.getY0() - (Y + renderer.getGenerationMatrix().getStep() / 2) / resolution;
                        }

                        IterationResult res = renderer.getIterationCalculator().computeIteration((realX),(realY));
                        ParameterResult parameterResult = renderer.getParametersCalculator().computeParameters(res);

                        int color = renderer.getPalette().getColorFromParam(parameterResult);

                        int n = res.getN();
                        if (renderer.getPool().getTarget() == ThreadPool.RenderingType.PREVIEW) {
                            renderer.getGenerationMatrix().setValAt(x, y, n == renderer.getIterationCalculator().getMaxIter());

                        }

                        for (int i = 0; i < step && X + i < p.getDX(); i++) {
                            for (int j = 0; j < step && j + Y < p.getDY(); j++) {

                                renderer.getMdbImage().setRGB(X + p.getX() + i, Y + p.getY() + j, color);
                                renderer.getParametersMatrix()[X + p.getX() + i][ Y + p.getY() + j]= parameterResult;

                            }
                        }

                    } else {

                        if (renderer.getPool().getTarget() == ThreadPool.RenderingType.RENDERING) {
                            //renderer.getMdbImage().setRGB(X+p.getX(), Y+p.getY(), Color.RED.getRGB());
                        }
                    }

                }

            }

            updateParcelRendererListenners();

        }
        stop = true;
        sendJobIsDone();
    }

    public void sendJobIsDone() {
        for (ParcelRendererListenner p : listeners) {
            p.jobIsDone();
        }
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

}
