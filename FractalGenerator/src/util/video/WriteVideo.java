/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.video;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.xuggler.IRational;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gregoire
 */
class WriteVideo implements Runnable {

        BufferedVideoWriter writer;
        IMediaWriter videoWriter ;
        private int nbOfFrames ; // time on top of the video Stack
        protected boolean jobDone ;

        public WriteVideo(BufferedVideoWriter writer) {
            this.writer = writer;
            this.videoWriter = writer.getWriter();
            nbOfFrames = 0;
            jobDone= false;

        }

        @Override
        public void run() {
            nbOfFrames =0;
           jobDone = false;
           int imagesToWrite=-1;
            while (!jobDone) {
                
                if(writer.isStop() && imagesToWrite == -1){
                    imagesToWrite = writer.getQueue().size();
                    
                }
                if(imagesToWrite == 0){
                    jobDone = true;
                }
                
                 if(!writer.getQueue().isEmpty()){
                     FrameJob toWrite =null;
                     try {
                          toWrite= writer.getQueue().pop();
                     } catch (Exception e) {
                         continue;
                     }
                     int nbToEncode = toWrite.getNbOfFrames();
                    
                     while(nbToEncode>0){
                        long l =(long)(nbOfFrames*writer.getFrame_rate().getDouble() );
                        videoWriter.encodeVideo(0, convertToType(toWrite.getImage(), BufferedImage.TYPE_3BYTE_BGR),l, TimeUnit.MILLISECONDS);
                        nbOfFrames++;
                        nbToEncode--;
                     }
                 }
                 else{
                     try {
                         Thread.sleep(1);
                     } catch (InterruptedException ex) {
                         Logger.getLogger(WriteVideo.class.getName()).log(Level.SEVERE, null, ex);
                     }
                     
                 }
            }
            jobDone = true;
          

        }
    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;

        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } // otherwise create a new image of the target type and draw the new
        // image
        else {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
    }

    public boolean isJobDone() {
        return jobDone;
    }

    public void setJobDone(boolean jobDone) {
        this.jobDone = jobDone;
    }
    
    public static void main(String[] args) {
        System.out.println(""+IRational.make(3, 1).getDouble());
        
    }
    
    }
