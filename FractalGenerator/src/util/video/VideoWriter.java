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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Gregoire
 */
public class VideoWriter {

    protected final IMediaWriter writer;
    protected IRational frame_rate = IRational.make(1000.0 / 15);
    protected int index;
    
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

    

    public VideoWriter(File output,int width,int height) {
        writer = ToolFactory.makeWriter(output.getPath());
        writer.addVideoStream(0, 0, ICodec.findDecodingCodec(ICodec.ID.CODEC_ID_H264),frame_rate,width,height);
        
        index=0;
    }

    public void addFrame(BufferedImage i) {
        writer.encodeVideo(0,
                convertToType(i, BufferedImage.TYPE_3BYTE_BGR),(int)(frame_rate.getDouble()*index),TimeUnit.MILLISECONDS);
        index++;
        
    }
    public void stop(){
        writer.close();
    }
    public static void main(String[] args) {
        IMediaWriter writer = ToolFactory.makeWriter("D:\\okairdsde.mp4");
     //   writer.close();
       
        IRational frame_rate = IRational.make(1000.0 / 60.0);
        writer.addVideoStream(0, 0, frame_rate, 1900, 2000);
        long d1 = System.nanoTime();
        for (int i = 0; i < 40*frame_rate.getDouble(); i++) {
            long s1 = System.nanoTime();
            BufferedImage img = new BufferedImage(1900, 2000, BufferedImage.TYPE_3BYTE_BGR);
            
            Graphics g = img.getGraphics();
            g.setColor(Color.GREEN);
            int w = (int)((double)i/(10*frame_rate.getDouble())*1900);
            g.fillRect(0, 0, w, 2000);
            long s = System.nanoTime();
            System.out.println(""+(double)(s-s1)/Math.pow(10, 9));
            writer.encodeVideo(0, img, (int)(frame_rate.getDouble()*i), TimeUnit.MILLISECONDS);
            double time = (System.nanoTime()-s)/Math.pow(10, 9);
            System.out.println("elapsed "+time);
        }
        writer.close();
    }
}
