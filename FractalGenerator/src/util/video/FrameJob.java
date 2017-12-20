/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.video;

import java.awt.image.BufferedImage;

/**
 *
 * @author Gregoire
 */
public class FrameJob {
    private int nbOfFrames ; 
    private BufferedImage image;

    public FrameJob(int nbOfFrames, BufferedImage image) {
        this.nbOfFrames = nbOfFrames;
        this.image = image;
    }

    public int getNbOfFrames() {
        return nbOfFrames;
    }

    public void setNbOfFrames(int nbOfFrames) {
        this.nbOfFrames = nbOfFrames;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    
    
}
