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

public class ParcelRendererTest implements Runnable {

    //protected MandelbrotRenderingConfig config;
    protected ArrayList<ParcelRendererListenner> listeners = new ArrayList();
    protected MdbRendererThreaded renderer;
    protected boolean stop;

    public ParcelRendererTest(MdbRendererThreaded renderer) {

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

        int solid = 1;
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
            for (int X = 0; X < p.getDX(); X += (step * solid)) {

                for (int Y = 0; Y < p.getDY(); Y += (step * solid)) {

                    if (this.renderer.getPool().isStop()) {
                        stop = true;
                        return;

                    }
                    ;


//                    if(renderer.getPool().getTarget() ==ThreadPool.RenderingType.PREVIEW ){
//                       
//                        for (int i = 0; i < step && X+i< p.getDX(); i++) {
//                            for (int j = 0; j < step && Y +j < p.getDY();j++) {
//                                 renderer.getMdbImage().setRGB(p.getX()+X+i, p.getY()+Y+j, Color.RED.darker().getRGB());
//                            }
//                        }
//                    }
                    int x = Math.min((int) Math.round((double) (p.getX() + X) / renderer.getGenerationMatrix().getStep()), maxX);
                    int y = Math.min((int) Math.round((double) (p.getY() + Y) / renderer.getGenerationMatrix().getStep()), maxY);

                    if (!renderer.getGenerationMatrix().getValAt(x, y)) {

                        int xDX = Math.min(X + step * solid, p.getDX() - 1);
                        int yDY = Math.min(Y + step * solid, p.getDY() - 1);

                        double realX, realX1, realY, realY1; //  x,y on Real plane

                        int dxFM = 0; // dx dy from Mode 
                        int dyFM = 0;
                        if (renderer.getPool().getTarget() == ThreadPool.RenderingType.PREVIEW) {
                            dxFM = renderer.getGenerationMatrix().getStep() / 2;
                            dyFM = renderer.getGenerationMatrix().getStep() / 2;
                        }

                        realX = p.getX0() + (X + dxFM) / resolution;
                        realY = p.getY0() - (Y + dyFM) / resolution;
                        realX1 = p.getX0() + (xDX + dxFM) / resolution;
                        realY1 = p.getY0() - (yDY + dyFM) / resolution;
                        IterationResult r0 = renderer.getIterationCalculator().computeIteration(realX, realY);
                        IterationResult r1 = renderer.getIterationCalculator().computeIteration(realX1, realY);
                        IterationResult r2 = renderer.getIterationCalculator().computeIteration(realX, realY1);
                        IterationResult r3 = renderer.getIterationCalculator().computeIteration(realX1, realY1);

                        ParameterResult p0 = renderer.getParametersCalculator().computeParameters(r0);
                        ParameterResult p1 = renderer.getParametersCalculator().computeParameters(r1);
                        ParameterResult p2 = renderer.getParametersCalculator().computeParameters(r2);
                        ParameterResult p3 = renderer.getParametersCalculator().computeParameters(r3);

                        int c0 = renderer.getPalette().getColorFromParam(p0),
                                c1 = renderer.getPalette().getColorFromParam(p1),
                                c2 = renderer.getPalette().getColorFromParam(p2),
                                c3 = renderer.getPalette().getColorFromParam(p3);

                        if (c0 == c1 && c1 == c2 && c2 == c3) {
                            for (int i = 0; i < solid * step && X + i < p.getDX(); i++) {
                                for (int j = 0; j < solid * step && j + Y < p.getDY(); j++) {
                                    // c0= (int)(Color.RED.getRGB()*Math.random());
                                    renderer.getMdbImage().setRGB(X + p.getX() + i, Y + p.getY() + j, c0);
                                    renderer.getParametersMatrix()[X + p.getX() + i][Y + p.getY() + j] = p0;
                                    x = Math.min((int) Math.round((double) (X + p.getX() + i) / renderer.getGenerationMatrix().getStep()), maxX);
                                    y = Math.min((int) Math.round((double) (p.getY() + Y + j) / renderer.getGenerationMatrix().getStep()), maxY);
                                    renderer.getGenerationMatrix().setValAt(x, y, r0.getN() == renderer.getIterationCalculator().getMaxIter());

                                }
                            }
                        } else {
                            renderer.getMdbImage().setRGB(p.getX() + X, p.getY() + Y, c0);
                            renderer.getMdbImage().setRGB(p.getX() + xDX, p.getY() + Y, c1);
                            renderer.getMdbImage().setRGB(p.getX() + X, p.getY() + yDY, c2);
                            renderer.getMdbImage().setRGB(p.getX() + xDX, p.getY() + yDY, c3);

                            renderer.getParametersMatrix()[p.getX() + X][p.getY() + Y] = p0;
                            renderer.getParametersMatrix()[p.getX() + xDX][p.getY() + Y] = p1;
                            renderer.getParametersMatrix()[p.getX() + X][p.getY() + yDY] = p2;
                            renderer.getParametersMatrix()[p.getX() + xDX][p.getY() + yDY] = p3;

                            x = Math.min((int) Math.round((double) (p.getX() + X) / renderer.getGenerationMatrix().getStep()), maxX);
                            y = Math.min((int) Math.round((double) (p.getY() + Y) / renderer.getGenerationMatrix().getStep()), maxY);

                            renderer.getGenerationMatrix().setValAt(x, y, r0.getN() == renderer.getIterationCalculator().getMaxIter());

                            x = Math.min((int) Math.round((double) (p.getX() + xDX) / renderer.getGenerationMatrix().getStep()), maxX);
                            y = Math.min((int) Math.round((double) (p.getY() + Y) / renderer.getGenerationMatrix().getStep()), maxY);

                            renderer.getGenerationMatrix().setValAt(x, y, r1.getN() == renderer.getIterationCalculator().getMaxIter());
                            x = Math.min((int) Math.round((double) (p.getX() + X) / renderer.getGenerationMatrix().getStep()), maxX);
                            y = Math.min((int) Math.round((double) (p.getY() + yDY) / renderer.getGenerationMatrix().getStep()), maxY);

                            renderer.getGenerationMatrix().setValAt(x, y, r2.getN() == renderer.getIterationCalculator().getMaxIter());
                            x = Math.min((int) Math.round((double) (p.getX() + X + xDX) / renderer.getGenerationMatrix().getStep()), maxX);
                            y = Math.min((int) Math.round((double) (p.getY() + yDY) / renderer.getGenerationMatrix().getStep()), maxY);

                            renderer.getGenerationMatrix().setValAt(x, y, r3.getN() == renderer.getIterationCalculator().getMaxIter());

                            for (int sX = 0; sX < solid; sX++) {
                                for (int sY = 0; sY < solid; sY++) {
                                    int subX = sX * step + X;
                                    int subY = sY * step + Y;
                                    realX = p.getX0() + (subX + dxFM) / resolution;
                                    realY = p.getY0() - (subY + dyFM) / resolution;

                                    IterationResult res = renderer.getIterationCalculator().computeIteration(realX, realY);
                                    ParameterResult parameterResult = renderer.getParametersCalculator().computeParameters(res);
                                    int color = renderer.getPalette().getColorFromParam(parameterResult);

                                    x = Math.min((int) Math.round((double) (subX + p.getX()) / renderer.getGenerationMatrix().getStep()), maxX);
                                    y = Math.min((int) Math.round((double) (subY + p.getY()) / renderer.getGenerationMatrix().getStep()), maxY);
                                    renderer.getGenerationMatrix().setValAt(x, y, res.getN() == renderer.getIterationCalculator().getMaxIter());

                                    for (int stepX = 0; stepX < step && stepX + subX < p.getDX(); stepX++) {
                                        for (int stepY = 0; stepY < step && stepY + subY < p.getDY(); stepY++) {

                                            renderer.getMdbImage().setRGB(subX + p.getX() + stepX, subY + p.getY() + stepY, color);
                                            renderer.getParametersMatrix()[subX + p.getX() + stepX][subY + p.getY() + stepY] = parameterResult;
                                        }
                                    }
                                }
                            }
                        }

//                        IterationResult res = renderer.getIterationCalculator().computeIteration(realX, realY);
//                        ParameterResult parameterResult = renderer.getParametersCalculator().computeParameters(res);
//
//                        int color = renderer.getPalette().getColorFromParam(parameterResult);
//
//                        int n = res.getN();
//                        if (renderer.getPool().getTarget() == ThreadPool.RenderingType.PREVIEW) {
//                            renderer.getGenerationMatrix().setValAt(x, y, n == renderer.getIterationCalculator().getMaxIter());
//
//                        }
//
//                        for (int k = 0; k < step && X + k < p.getDX(); k++) {
//                            for (int l = 0; l < step && l + Y < p.getDY(); l++) {
//
//                                renderer.getMdbImage().setRGB(X + p.getX() + k, Y + p.getY() + l, color);
//                                renderer.getParametersMatrix()[X + p.getX() + k][Y + p.getY() + l] = parameterResult;
//
//                            }
//                        }
                    } else {

                        if (renderer.getPool().getTarget() == ThreadPool.RenderingType.RENDERING) {
//                              for (int k = 0; k < 5 && X + k < p.getDX(); k++) {
//                            for (int l = 0; l < 5 && l + Y < p.getDY(); l++) {
//
//                                renderer.getMdbImage().setRGB(X + p.getX() + k, Y + p.getY() + l, Color.RED.getRGB());
//
//                            }
//                        }

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
