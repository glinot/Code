package mandelbrot.gui;

// @author Raphaël

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import mandelbrot.controller.Controller;


public class ImageDisplayer extends JPanel
{
    private final Controller controller;
    private Field imageField;
    
    public ImageDisplayer(Controller controller, String imageFieldName)
    {
        this.controller = controller;
        
        try
        {
            this.imageField = Controller.class.getField(imageFieldName);
        }
        catch(NoSuchFieldException | SecurityException ex)
        {
            Logger.getLogger(ImageDisplayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        try
        {
            if(imageField.get(controller) != null)
            {
                BufferedImage bufferedImage = ((BufferedImage)imageField.get(controller));
                int newHeight =getHeight();
                double reduction  =(double) newHeight/ bufferedImage.getHeight();
                 int newWidth =(int)( bufferedImage.getWidth()*reduction);
                 
                 int mX = (int)(newWidth/2.0);  
                 int MX = (int)(getWidth()/2.0);
                g.drawImage(bufferedImage.getScaledInstance(newWidth,newHeight, Image.SCALE_FAST), Math.max(0, MX-mX), 0, null);
            }
        }
        catch(IllegalArgumentException | IllegalAccessException ex)
        {
            Logger.getLogger(ImageDisplayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Controller getController()
    {
        return controller;
    }
    
    public Field getImageField()
    {
        return imageField;
    }
    
    public void setImageField(Field imageField)
    {
        this.imageField = imageField;
    }
    
    public void setImageField(String imageFieldName)
    {
        try
        {
            this.imageField = Controller.class.getField(imageFieldName);
        }
        catch(NoSuchFieldException | SecurityException ex)
        {
            Logger.getLogger(ImageDisplayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
