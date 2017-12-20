/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot.rendering.threading;

import java.util.ArrayList;
import mandelbrot.rendering.parameterscalculator.ParameterResult;

/**
 *
 * @author Gregoire
 */
public class ImageRenderer {

    private MdbRendererThreaded renderer;

    private ArrayList<RendererWorker> workers;
    private final int nbWidth = 5, nbHeight = 5;

    public ImageRenderer(MdbRendererThreaded renderer) {
        this.renderer = renderer;
        workers= new ArrayList<>();
    }

    public void renderImage() {
        if (workers.size() != 0) {
            stopRenderering();
            waitEndOfWorkers();
        }
        workers.clear();
        int stepX = renderer.getParametersMatrix().length/nbWidth;
        int stepY = renderer.getParametersMatrix()[0].length/nbHeight;
        for (int i = 0; i < nbWidth; i++) {
            for (int j = 0; j < nbHeight; j++) {
                int sX = i*stepX;
                int sY= j*stepY;
                int eX = Math.min((i+1)*stepX,renderer.getParametersMatrix().length);
                int eY = Math.min((j+1)*stepY,renderer.getParametersMatrix()[0].length );
                workers.add(new RendererWorker(sX, eX, sY, eY));
            }
        }
        for (RendererWorker worker : workers) {
            new Thread(worker).start();
        }

    }

    public void stopRenderering() {
        for (int i = 0; i < workers.size(); i++) {
            workers.get(i).stop();
        }
    }

    public void waitEndOfWorkers() {
        boolean go = false;
        while (!go) {
            go = true;
            for (RendererWorker worker : workers) {
                if (!worker.isStopped()) {
                    go = false;
                }
            }
        }
    }

    class RendererWorker implements Runnable {

        boolean stop = false;
        boolean stopped = false;
        int sX, eX, sY, eY;

        public RendererWorker(int sX, int eX, int sY, int eY) {
            this.sX = sX;
            this.eX = eX;
            this.sY = sY;
            this.eY = eY;
        }

        @Override
        public void run() {
            stop = stopped = false;

            for (int i = sX; i < eX && !stop && i < renderer.getParametersMatrix().length; i++) {
                for (int j = sY; j < eY && !stop && j < renderer.getParametersMatrix()[i].length; j++) {
                    ParameterResult res = renderer.getParametersMatrix()[i][j];
                    
                    if(res != null && renderer != null && renderer.getPalette() != null && renderer.getMdbImage() != null ){
                        try {
                            renderer.getMdbImage().setRGB(i, j, renderer.getPalette().getColorFromParam(res));
                        } catch (Exception e) {
                        }
                        
                        
                    }
                   
                }
            }
            renderer.sendCanRepaintSignal(); 
            stopped = true;
        }

        public void stop() {
            stop = true;
        }

        public boolean isStopped() {
            return stopped;
        }

    }
}
