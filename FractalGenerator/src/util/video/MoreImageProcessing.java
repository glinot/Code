/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.video;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Gregoire
 */
public class MoreImageProcessing {

    private static boolean isInBounds(BufferedImage img, int i, int j) {
        return i >= 0 && j >= 0 && i < img.getWidth() && j < img.getHeight();
    }

    public static BufferedImage smoothGradient(BufferedImage i, double weight) {
        BufferedImage res = new BufferedImage(i.getWidth(), i.getHeight(), i.getType());
        for (int j = 0; j < res.getWidth(); j++) {
            for (int k = 0; k < res.getHeight(); k++) {
                int r, g, b;
                r = g = b = 0;
                int nb = 0;
                if (isInBounds(i, j - 1, k)) {
                    Color col = new Color(i.getRGB(j - 1, k));
                    r+=col.getRed();
                    g+=col.getGreen();
                    b += col.getBlue();
                    nb++;
                }
                if (isInBounds(i, j + 1, k)) {
                    Color col = new Color(i.getRGB(j + 1, k));
                    r+=col.getRed();
                    g+=col.getGreen();
                    b += col.getBlue();
                    nb++;
                }
                if (isInBounds(i, j, k - 1)) {
                    Color col = new Color(i.getRGB(j , k-1));
                     r+=col.getRed();
                    g+=col.getGreen();
                    b += col.getBlue();
                    nb++;
                }
                if (isInBounds(i, j, k + 1)) {
                    Color col = new Color(i.getRGB(j , k+1));
                     r+=col.getRed();
                    g+=col.getGreen();
                    b += col.getBlue();
                    nb++;
                }
                
                Color cC =new Color( i.getRGB(j, k));
                System.out.println(""+(double)r/nb);
                r=(int)(weight*(double)r/nb+cC.getRed()*(1-weight));
                g=(int)(weight*(double)g/nb+cC.getGreen()*(1-weight));
                b=(int)(weight*(double)b/nb+cC.getBlue()*(1-weight));
//                int color = (int)(i.getRGB(j, k)*(1-weight)+toAdd*weight);
                int color = new Color(r, g, b).getRGB();
                res.setRGB(j, k, color);
            }

        }
        return res;
    }
    public static BufferedImage gaussian_blur(BufferedImage buff, int radius){
        BufferedImage img = new BufferedImage(buff.getWidth(), buff.getHeight(),buff.getType());
        int rs =(int) Math.ceil(radius*2.57);
        for (int i = 0; i < buff.getWidth(); i++) {
            for (int j = 0; j < buff.getHeight(); j++) {
                int wsum =0;
                float valR=0,valG=0,valB =0;
                for (int k = i-rs; k < i+rs+1; k++) {
                    for (int l = j-rs; l < j+rs+1; l++) {
                        int x = Math.min(buff.getWidth()-1, Math.max(0, k)) ;
                        int y = Math.min(buff.getHeight()-1,Math.max(0, l));
                        int dsq = (k-j)*(k-j)+(l-i)*(l-i);
                        double weight = Math.exp(-dsq/(2*radius*radius))/(Math.PI*2*radius*radius);
                        Color c  = new Color(buff.getRGB(x, y));
                        valR+= c.getRed()*weight;
                        valG+=c.getGreen()*weight;
                        valB+=c.getBlue()*weight;
                        wsum+=weight;
                        
                    }
                }
                img.setRGB(i, j, new Color(Math.round(valR/wsum)%255, Math.round(valG/wsum)%255, Math.round(valB/wsum)%255).getRGB());
            }
        }
        return img;
    }
    
    
    

    public static void main(String[] args) throws IOException {
        BufferedImage i = ImageIO.read(new File("D:\\img2.jpg"));
        ImageIO.write(gaussian_blur(i, 1), "png", new File("D:\\testSm2ss.png"));
    }
}
