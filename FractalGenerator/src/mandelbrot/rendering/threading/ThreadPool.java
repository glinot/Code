    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot.rendering.threading;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.video.MoreImageProcessing;

/**
 *
 * @author Gregoire
 */
public class ThreadPool implements ParcelRendererListenner {

    protected boolean stop = false;
    protected volatile ArrayDeque<Parcel> queue;
    protected MdbRendererThreaded callerRenderer;
    protected ArrayList<Thread> threads;
    protected int nbThreadOver = 0;
    protected boolean jobIsDone;
    protected ArrayList<ParcelRenderer> renderers;
    protected int nbParcellRendered;
    protected int lastQueueSize = 0;

    public static enum RenderingType {

        PREVIEW, RENDERING
    };
    private RenderingType target;

    private int step;

    public ThreadPool(ArrayList<Parcel> parcels, MdbRendererThreaded caller) {
        jobIsDone = false;
        queue = new ArrayDeque<>();
        this.callerRenderer = caller;
        for (Parcel p : parcels) {
            queue.push(p);
        }
        nbThreadOver = 0;
        jobIsDone = false;
        threads = new ArrayList<>();
        renderers = new ArrayList<>();

        target = RenderingType.RENDERING;
        renderers.clear();
        for (int i = 0; i < callerRenderer.getThreadsNbr(); i++) {
            ParcelRenderer r = new ParcelRenderer(callerRenderer);

            r.addParcelRendererListenner(this);
            renderers.add(r);
        }

    }

    public void start(RenderingType target) {
        nbParcellRendered = 0;
        lastQueueSize = queue.size();
        for (ParcelRenderer r : renderers) {
            r.setStop(false);
        }
        this.target = target;
        if (target == RenderingType.RENDERING) {
            step = 1;
        }
        threads.clear();
        stop = false;
        jobIsDone = false;
        for (int i = 0; i < callerRenderer.getThreadsNbr() && i < renderers.size(); i++) {
            Thread t = new Thread(renderers.get(i));
            threads.add(t);
            t.start();
        }

    }

    /**
     *
     */
    public void waitEndOfWork() {
        while (!areAllRendererStopped()) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadPool.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Blocking Wait till Pool is Being fully interrupted
     */
    public void stopPool() {
        stop = true;
        waitEndOfWork();

    }

    private boolean areAllRendererStopped() {
        for (ParcelRenderer p : renderers) {
            if (!p.isStop()) {
                return false;
            }
        }
        return true;
    }

    public void addJob(Parcel p) {
        queue.push(p);

    }

    public void addJobs(ArrayList<Parcel> parcels) {
        for (Parcel p : parcels) {
            queue.push(p);
        }
    }

    public void removeJobs() {
        this.callerRenderer.getQueueAccess().lock();
        this.queue.clear();
        this.callerRenderer.getQueueAccess().unlock();
    }

    @Override
    public void parcelRendered() {

        nbParcellRendered++;
        callerRenderer.sendCanRepaintSignal();
    }

    @Override
    public void queueIsEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public ArrayDeque<Parcel> getQueue() {
        return queue;
    }

    public void setQueue(ArrayDeque<Parcel> queue) {
        this.queue = queue;
    }

    public MdbRendererThreaded getCallerRenderer() {
        return callerRenderer;
    }

    public void setCallerRenderer(MdbRendererThreaded callerRenderer) {
        this.callerRenderer = callerRenderer;
    }

    public ArrayList<Thread> getThreads() {
        return threads;
    }

    public void setThreads(ArrayList<Thread> threads) {
        this.threads = threads;
    }

    @Override
    public void jobIsDone() {

        nbThreadOver++;
        callerRenderer.updateProgessBars((double) (nbParcellRendered) / lastQueueSize);
        if (nbThreadOver == this.callerRenderer.getThreadsNbr()) {
            jobIsDone = true;
        }

    }

    public boolean isJobIsDone() {
        return jobIsDone;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void addParcells(ArrayList<Parcel> list) {
        for (Parcel p : list) {
            queue.push(p);
        }
    }

    public RenderingType getTarget() {
        return target;
    }

    public void setTarget(RenderingType target) {
        this.target = target;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

}
