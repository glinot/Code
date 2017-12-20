/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.audio;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import mandelbrot.gui.SoundSpectrumViewer;

/**
 * This class is supposed to play audio and give the fft
 *
 * @author Gregoire
 */
public class MandelAudioPlayer {

    /**
     * List of listenners waiting for new FFT
     *
     */
    private ArrayList<MandelAudioPlayerListenner> listenners;

    static {
        minim = new Minim(new MinimLoader());
    }

    /**
     * static object of Minim for loading and creating AudioPlayer
     */
    private static Minim minim;

    /*
     t_delta intervall of time for the timer
     values in milliseconds*/
    private long t_delta;
    /**
     * timer for listenners
     */
    private Timer timer_listenners;
    /**
     * file to read
     */
    private File file;

    /**
     * audio player for reading file and applying fft
     */
    private AudioPlayer player;

    /**
     * FFT class to compute fft
     */
    private FFT fft;

    /**
     * CHUNK SIZE for the player
     */
    private final int CHUNK_SIZE = 1024;

    private final int LIN_AVERAGE_NUM = 20;

    /**
     *
     * @param t_delta intervall between two fft results
     * @param file file to be read
     */
    public MandelAudioPlayer(long t_delta) {
        this.t_delta = t_delta;
        this.file = file;
        
        this.listenners = new ArrayList<>();

    }

    public void start() {
        if (file != null) {
            this.player = minim.loadFile(file.getPath(), CHUNK_SIZE);
            player.loop();
            fft = new FFT(player.bufferSize(), player.sampleRate());
            fft.linAverages(LIN_AVERAGE_NUM);

            //start timer 
            timer_listenners = new Timer("Timer AudioPlayerListenners");
            timer_listenners.scheduleAtFixedRate(createTimerTask(), 0, t_delta);
        }

    }

    /**
     * Stops the current player
     */
    public void stop() {
        player.close();
    }

    public void load(File f) {
        if(this.player != null){
            player.close();
        }
        this.file = f;
        this.player = minim.loadFile(f.getPath(), CHUNK_SIZE);

    }

    /**
     *
     * @return custom TimerTask to be executed by the timer the task computes
     * the FFT of the player's audio buffer and send a notifiaction to all
     * Listenners
     */
    public TimerTask createTimerTask() {
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                //compute FFT
                fft.forward(player.mix);
                // send fft to all listenners
                notifyAllListennerOfNewFFT();

            }
        };
        return timerTask;
    }

    /**
     *
     * @param l add new MandelAudioPlayerListenner
     */
    public void addListenner(MandelAudioPlayerListenner l) {
        this.listenners.add(l);

    }

    /**
     *
     * @param l remove a MandelAudioPlayerListenner
     */
    public void removeListenner(MandelAudioPlayerListenner l) {
        this.listenners.remove(l);
    }

    /**
     * Notifies all listenner that a new FFT of Player's buffer has been
     * computed
     */
    public void notifyAllListennerOfNewFFT() {

        for (MandelAudioPlayerListenner listenner : listenners) {
            listenner.onNewFFT(fft);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MandelAudioPlayer m = new MandelAudioPlayer(10);
        m.load( new File("D:\\test3.mp3"));
        m.start();
         SoundSpectrumViewer viewer = new SoundSpectrumViewer(500);
        viewer.setVisible(true);
        JFrame j = new JFrame("coool");
        j.setContentPane(viewer);
        j.setVisible(true);
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.setPreferredSize(new Dimension(600, 600));
        j.pack();
        MandelAudioPlayerListenner l = new MandelAudioPlayerListenner() {

            @Override
            public void onNewFFT(FFT fft) {

                int n = 100;
                double[] res = new double[n];
                for (int i = 0; i < n; i++) {
                    res[i] = fft.getBand(i);
                }
                viewer.setModCoeff(res);
                viewer.repaint();

            }
        };
        m.addListenner(l);
    }

}
