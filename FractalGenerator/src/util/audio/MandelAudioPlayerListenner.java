/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.audio;

import ddf.minim.analysis.FFT;

/**
 *
 * @author Gregoire
 */
public interface MandelAudioPlayerListenner {
    public void onNewFFT(FFT fft);
}
