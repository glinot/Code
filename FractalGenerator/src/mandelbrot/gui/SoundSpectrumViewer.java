package mandelbrot.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import javax.swing.JPanel;
import static java.lang.Math.sin;
import static java.lang.Math.cos;

/**
 * @author Raphaël
 */
public class SoundSpectrumViewer extends JPanel {

    private int j_inc=0;
    protected double[] modCoeff;
    protected double modMax;

    public SoundSpectrumViewer(double modMax) {
        this.modMax = Math.abs(modMax);
        //this.setBackground(new Color(44, 62, 80));
        this.setBackground(new Color(0, 0, 0));
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);
        if (modCoeff != null) {
            int width = getWidth(),
                    height = getHeight(),
                    n = Math.min(modCoeff.length, width),
                    barWidth = width / n;

//            double average = 0;
//            int NB = 10;
//            for (int i = 0; i < NB; i++) {
//                average +=  modCoeff[i]*modCoeff[i];
//            }
//            average = Math.sqrt(average)/NB;
//            int radius = (int)((average/(double)modMax)*width)*60;
//            int color=(int)(average*255);
//            g2d.setColor(new Color(241, 196,color%256));
//            if(color == 0){
//                System.out.println("ModMax " +modMax+" avg "
//                +average);
//            }
//            g2d.fillOval(getWidth()/2-radius/2, getHeight()-radius/2, radius, radius);
//            
            double d_theta = 2 * Math.PI / modCoeff.length;

            double p = 0.10;
            int radius = (int) (p * width);

            int xC = width / 2, yC = height / 2;

           
            

            int x1, y1, x2, y2, x3, y3, x4, y4;
            int amplitude = 100, i = j_inc+=((int)Math.random()*10);
            
            double maxSize = (height/2)*(1-p);
            
            for (double theta =0; theta < (2 * Math.PI); theta += d_theta) {
                amplitude = (int)(modCoeff[i%modCoeff.length]/modMax*maxSize*5);
                x1 = (int) (xC + radius * cos(theta));
                y1 = (int) (yC - radius * sin(theta));

                x2 = (int) (xC + radius * cos(theta+d_theta));
                y2 = (int) (yC - radius * sin(theta+d_theta));
                
                x3 = (int) (x1 + amplitude*cos(theta));
                y3 = (int) (y1 - amplitude* sin(theta));
                
                x4 = (int) (x2 + amplitude*cos(theta+d_theta*0));
                y4 = (int) (y2 - amplitude* sin(theta+d_theta*0));
                
             
                g2d.setColor(Color.WHITE);
//                g2d.drawLine(x1, y1, x3, y3);
//                g2d.drawLine(x2, y2, x4, y4);
//                g2d.drawLine(x1, y1, x2, y2);
//                g2d.drawLine(x3, y3, x4, y4);
                g2d.fillPolygon(new int[]{x1,x2,x4,x3}, new int[]{y1,y2,y4,y3},4);
                g2d.setColor(new Color(192, 57, 43));
                g2d.drawPolygon(new int[]{x1,x2,x4,x3}, new int[]{y1,y2,y4,y3},4);
                
                i++;

            }

            
            int lastX = 0, lastY = 0;

            for (i = 0; i < n; i++) {
                amplitude = (int) Math.round(modCoeff[i] / modMax * height);
                g.setColor(Color.WHITE);
                Stroke s = g2d.getStroke();
                g2d.setStroke(new BasicStroke(2));
                g.fillRect(i * barWidth, height - amplitude, barWidth, amplitude);
                g2d.setColor(new Color(192, 57, 43));
                g.drawRect(i * barWidth, height - amplitude, barWidth, amplitude);

//                g2d.setStroke(new BasicStroke(4));
//               g2d.setColor(new Color(241, 196, 15));
//                g2d.drawLine(lastX, lastY,i*barWidth,height-amplitude);
//                lastX =i*barWidth+barWidth/2;
//                lastY=height-amplitude;
                g2d.setStroke(s);

            }

        }
    }

    public void setModCoeff(double[] modCoeff) {
        this.modCoeff = modCoeff;
        repaint();
    }

    public void setModMax(double modMax) {
        this.modMax = modMax;
    }
}
