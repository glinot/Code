package graphicalsystem;

// @author Raphaël

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GraphicalSystem extends JPanel
{
    private GraphicalObjectList objList;
    private final Timer refreshmentTimer;
    private int timerCount;
    
    public GraphicalSystem()
    {
        this.objList = null;
        this.refreshmentTimer = new Timer(10, null);
        this.refreshmentTimer.setRepeats(true);
        this.refreshmentTimer.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                timerCount++;
                
                if(objList.checkForChange(refreshmentTimer.getDelay()))
                {
                    repaint();
                }
            }
        });
        
        /*Class[] interfaces = this.getClass().getInterfaces();
        
        if(interfaces[3] == CollisionListener.class)
        {
            ((CollisionListener)this).collisionDetected(45);
        }*/
        
        this.timerCount = 0;
    }
    
    public void repaintGraphicalObjects(Graphics g)
    {
        this.objList.draw(g);
    }
    
    public GraphicalObjectList getGraphicalObjectList()
    {
        return this.objList;
    }
    
    public int getRefreshmentDelay()
    {
        return this.refreshmentTimer.getDelay();
    }
    
    public int getGlobalClockCount()
    {
        return this.timerCount;
    }
    
    public void setGraphicalObjectList(GraphicalObjectList objList)
    {
        this.objList = objList;
    }
    
    public void setRefreshmentDelay(int refreshmentDelay)
    {
        this.refreshmentTimer.setDelay(refreshmentDelay);
    }
    
    public void startGlobalClock()
    {
        this.refreshmentTimer.start();
    }
    
    public void stopGlobalClock()
    {
        this.refreshmentTimer.stop();
    }
}
