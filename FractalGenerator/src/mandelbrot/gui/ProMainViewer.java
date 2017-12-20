package mandelbrot.gui;

// @author Raphaël

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import mandelbrot.controller.Controller;
import graphicalsystem.GraphicalSystem;


public class ProMainViewer extends GraphicalSystem
{
    private final Controller controller;
    
    public ProMainViewer()
    {
        controller = new Controller();
    }
    
    public ProMainViewer(Controller controller)
    {
        this.controller = controller;
        addMouseListener(controller);
        addMouseMotionListener(controller);
        addMouseWheelListener(controller);
        addComponentListener(controller);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        BufferedImage img = controller.getExploRenderer().getMdbImage();
        
        if(img != null)
        {
            if(controller.getResizingTimer().isRunning())
            {
                int newWidth = getHeight()*img.getWidth()/img.getHeight();
                g.drawImage(
                        img.getScaledInstance(newWidth, getHeight(), Image.SCALE_FAST),
                        (getWidth()-newWidth)/2, 0, null);
            }
            else
            {
                g.drawImage(img, 0, 0, null);
            }
        }
        
        if(controller.getMdbViewerMousePos() != null)
        {
            int rectDX = (int)(controller.getExploRenderer().getWidth()/controller.getZoomMultiplier()),
                rectDY = (int)(controller.getExploRenderer().getHeight()/controller.getZoomMultiplier());

            g.setColor(Color.WHITE);
            g.drawRect(controller.getMdbViewerMousePos().getAbs()-rectDX/2,
                    controller.getMdbViewerMousePos().getOrd()-rectDY/2, rectDX, rectDY);
        }
    }
}
